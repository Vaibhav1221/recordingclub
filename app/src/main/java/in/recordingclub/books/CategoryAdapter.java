package in.recordingclub.books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import in.recordingclub.R;
import in.recordingclub.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CategoryModel category);
    }

    private List<CategoryModel> list;
    private OnItemClickListener listener;

    public CategoryAdapter(List<CategoryModel> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textViewCategoryName);
        }
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CategoryModel model = list.get(position);
        holder.name.setText(model.getName());

        // Handle click with up-to-date model
        holder.itemView.setOnClickListener(v -> listener.onItemClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
