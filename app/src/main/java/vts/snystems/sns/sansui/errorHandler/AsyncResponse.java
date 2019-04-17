package vts.snystems.sns.sansui.errorHandler;

public interface AsyncResponse<T> {

	 void processFinish(T output);
	 
	 void processFinishLog(T output);
	 
}
