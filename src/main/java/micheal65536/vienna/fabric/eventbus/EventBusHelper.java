package micheal65536.vienna.fabric.eventbus;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import micheal65536.vienna.eventbus.client.EventBusClient;
import micheal65536.vienna.eventbus.client.Publisher;
import micheal65536.vienna.eventbus.client.RequestSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class EventBusHelper
{
	private final String queueName;
	private final EventBusClient eventBusClient;
	private final Publisher publisher;
	private final RequestSender requestSender;

	public EventBusHelper(@NotNull String address, @NotNull String queueName)
	{
		this.queueName = queueName;

		try
		{
			this.eventBusClient = EventBusClient.create(address);
		}
		catch (EventBusClient.ConnectException exception)
		{
			throw new EventBusException("Could not connect to event bus", exception);
		}

		this.publisher = this.eventBusClient.addPublisher();
		this.requestSender = this.eventBusClient.addRequestSender();
	}

	public void close()
	{
		this.requestSender.flush();
		this.requestSender.close();
		this.publisher.flush();
		this.publisher.close();
		this.eventBusClient.close();
	}

	public void publishJson(@NotNull String type, Object messageObject)
	{
		CompletableFuture<Boolean> completableFuture = this.publisher.publish(this.queueName, type, new Gson().newBuilder().serializeNulls().create().toJson(messageObject));
		boolean success;
		for (; ; )
		{
			try
			{
				success = completableFuture.get();
				break;
			}
			catch (ExecutionException exception)
			{
				throw new AssertionError(exception);
			}
			catch (InterruptedException exception)
			{
				continue;
			}
		}
		if (!success)
		{
			throw new EventBusException("Could not publish message");
		}
	}

	@NotNull
	public <T> CompletableFuture<T> doRequestResponseAsync(@NotNull String requestType, Object requestObject, @NotNull Class<T> responseClass)
	{
		CompletableFuture<T> completableFuture = new CompletableFuture<>();

		Gson gson = new Gson().newBuilder().serializeNulls().create();
		this.requestSender.request(this.queueName, requestType, gson.toJson(requestObject)).thenAccept(responseString ->
		{
			if (responseString == null)
			{
				completableFuture.completeExceptionally(new EventBusException("Did not receive response"));
			}
			else
			{
				try
				{
					T response = new Gson().fromJson(responseString, responseClass);
					completableFuture.complete(response);
				}
				catch (Exception exception)
				{
					completableFuture.completeExceptionally(new EventBusException("Error receiving response", exception));
				}
			}
		});

		return completableFuture;
	}

	@NotNull
	public <T> T doRequestResponseSync(@NotNull String requestType, Object requestObject, @NotNull Class<T> responseClass)
	{
		CompletableFuture<T> completableFuture = this.doRequestResponseAsync(requestType, requestObject, responseClass);
		for (; ; )
		{
			try
			{
				T response = completableFuture.get();
				if (response == null)
				{
					throw new EventBusException("Received null response for request");
				}
				return response;
			}
			catch (ExecutionException exception)
			{
				throw new EventBusException("Request-response completed with exception", exception);
			}
			catch (InterruptedException exception)
			{
				continue;
			}
		}
	}

	public static final class EventBusException extends RuntimeException
	{
		private EventBusException(String message)
		{
			super(message);
		}

		private EventBusException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}