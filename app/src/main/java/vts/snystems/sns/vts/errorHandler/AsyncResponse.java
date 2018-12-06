package vts.snystems.sns.vts.errorHandler;

public interface AsyncResponse<T> {

	 void processFinish(T output);
	 
	 void processFinishLog(T output);
	 
}
