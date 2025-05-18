package in.recordingclub.books;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import in.recordingclub.R;
import in.recordingclub.models.BookModel;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookModel> bookList;
    private FragmentActivity activity;

    public BookAdapter(List<BookModel> bookList, FragmentActivity activity) {
        this.bookList = bookList;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bookName;

        public ViewHolder(View view) {
            super(view);
            bookName = view.findViewById(R.id.textViewBookName);
        }
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        BookModel book = bookList.get(position);
        holder.bookName.setText(book.getName());

        holder.itemView.setOnClickListener(v -> {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ChapterFragment.newInstance(book.getId(), book.getName()))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
