package curtin.edu.onlineimageselector;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class OnlineImageAdapter extends RecyclerView.Adapter<OnlineImageAdapter.OnlineImageViewHolder> {
    private List<Bitmap> bitmapList = Collections.emptyList();
    private int selectedPosition = RecyclerView.NO_POSITION;
    OnlineSelectionViewModel onlineSelectionViewModel = null;

    @NonNull
    @Override
    public OnlineImageAdapter.OnlineImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnlineImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineImageAdapter.OnlineImageViewHolder holder, int position) {
        Bitmap bitmap = bitmapList.get(position);
        holder.onlineImage.setImageBitmap(bitmap);
        holder.imageItem.setSelected(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public void setData(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
        notifyDataSetChanged();
    }

    public void setOnlineSelectionViewModel(OnlineSelectionViewModel onlineSelectionViewModel) {
        this.onlineSelectionViewModel = onlineSelectionViewModel;
    }

    public class OnlineImageViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout imageItem;
        ImageView onlineImage;
        public OnlineImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imageItem);
            onlineImage = itemView.findViewById(R.id.ivFoundImage);

            onlineImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap selectedBitmap = bitmapList.get(getAdapterPosition());

                    // set the selected item
                    int oldPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();

                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);

                    onlineSelectionViewModel.setSelectedImage(selectedBitmap);
                }
            });
        }
    }
}