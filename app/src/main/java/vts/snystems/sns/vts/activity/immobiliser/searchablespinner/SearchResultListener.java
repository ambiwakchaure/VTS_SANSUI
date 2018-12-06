package vts.snystems.sns.vts.activity.immobiliser.searchablespinner;

/**
 * Created by MADNESS on 5/14/2017.
 */

public interface SearchResultListener<T> {
	void onSelected(BaseSearchDialogCompat dialog, T item, int position);
}
