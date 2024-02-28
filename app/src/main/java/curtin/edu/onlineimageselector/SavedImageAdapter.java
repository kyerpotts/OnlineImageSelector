package curtin.edu.onlineimageselector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SavedImageAdapter extends RecyclerView.Adapter<SavedImageAdapter.SavedImageViewHolder> {

    private List<String> savedImages;

    @NonNull
    @Override
    public SavedImageAdapter.SavedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SavedImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SavedImageAdapter.SavedImageViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(savedImages.get(position)).into(holder.ivFoundImage);
    }

    @Override
    public int getItemCount() {
        return savedImages.size();
    }

    public void setData(List<String> savedImages) {
        this.savedImages = savedImages;
        notifyDataSetChanged();
    }

    public class SavedImageViewHolder extends RecyclerView.ViewHolder{
        ImageView ivFoundImage;
        public SavedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoundImage = itemView.findViewById(R.id.ivFoundImage);
        }
    }
}
