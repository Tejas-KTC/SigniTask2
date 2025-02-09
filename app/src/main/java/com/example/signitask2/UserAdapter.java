package com.example.signitask2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserData> users = new ArrayList<>();
    private OnItemClickListener listener;
    private boolean isMultiSelectMode = false;
    private List<UserData> selectedUsers = new ArrayList<>();

    public UserAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserData user = users.get(position);
        holder.tvName.setText(user.getName());

        holder.ivSelect.setVisibility(selectedUsers.contains(user) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                toggleSelection(user);
            } else if (listener != null) {
                listener.onItemClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            isMultiSelectMode = true;
            toggleSelection(user);
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<UserData> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public List<UserData> getSelectedUsers() {
        return selectedUsers;
    }

    public void clearSelection() {
        selectedUsers.clear();
        isMultiSelectMode = false;
        notifyDataSetChanged();
    }

    private void toggleSelection(UserData user) {
        if (selectedUsers.contains(user)) {
            selectedUsers.remove(user);
        } else {
            selectedUsers.add(user);
        }

        // Notify MainActivity about selection changes
        if (listener != null) {
            listener.onItemSelectionChanged(selectedUsers.size());
        }

        if (selectedUsers.isEmpty()) {
            isMultiSelectMode = false;
        }

        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivSelect;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivSelect = itemView.findViewById(R.id.ivSelect);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserData user);
        void onItemSelectionChanged(int selectedCount);
    }
}
