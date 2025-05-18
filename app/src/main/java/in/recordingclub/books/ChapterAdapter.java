package in.recordingclub.books;

import in.recordingclub.R;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.recordingclub.models.ChapterModel;
import in.recordingclub.my_player.RC_Player;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

    private List<ChapterModel> list;
    private String[] file_name, file_link;
    private String book_name;
    private FragmentActivity activity;

    public ChapterAdapter(List<ChapterModel> list, String[] file_name, String[] file_link, String book_name, FragmentActivity activity) {
        this.list = list;
        this.file_name = file_name;
        this.file_link = file_link;
        this.book_name = book_name;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textViewChapterTitle);
        }

        public void bind(ChapterModel chapter) {
            title.setText(chapter.getTitle());
        }
    }

    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RC_Player.class);
                intent.putExtra("file_title", file_name);
                intent.putExtra("book", book_name);
                intent.putExtra("file", file_link);
                intent.putExtra("current_position", position);
                intent.putExtra("isBook", true);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
