package com.github.codetanzania.open311.android.library.ui.listview;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.ui.R;
import com.github.codetanzania.open311.android.library.utils.DateUtils;

import java.util.List;

public class ProblemListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Problem> mProblems;
    private final OnItemClickListener mClickListener;

    public ProblemListAdapter(List<Problem> problems,
                              OnItemClickListener onItemClickListener) {

        mProblems = problems;
        mClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_problem_list, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Problem problem = this.mProblems.get(position);
        ProblemViewHolder srViewHolder = (ProblemViewHolder) holder;

        // to the left of each row is a circle, colored by status, with the category code
        srViewHolder.tvCategoryIcon.setText(problem.getCategory().getCode());
        int categoryIconColor;
        try {
            categoryIconColor = Color.parseColor(problem.getStatus().getColor());
        } catch (Exception e) {
            categoryIconColor = ContextCompat.getColor(srViewHolder.cardView.getContext(),
                    problem.getStatus().isOpen() ? R.color.open : R.color.resolved);
        }
        DrawableCompat.setTint(srViewHolder.tvCategoryIcon.getBackground(), categoryIconColor);

        // the right text contains other relevant information
        srViewHolder.tvTitle.setText(problem.getCategory().getName());
        srViewHolder.tvTicketID.setText(problem.getTicketNumber());
        srViewHolder.tvDescription.setText(problem.getDescription());
        srViewHolder.tvDateCreated.setText(DateUtils.formatForDisplay(problem.getCreatedAt()));

        // hook up on item click listener
        srViewHolder.bind(problem, mClickListener);
    }

    @Override
    public int getItemCount() {
        return mProblems.size();
    }

    static class ProblemViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView tvCategoryIcon;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvDateCreated;
        TextView tvTicketID;

        ProblemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            tvCategoryIcon = (TextView) itemView.findViewById(R.id.tv_categoryIcon);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_problemTitle);
            tvTicketID = (TextView) itemView.findViewById(R.id.tv_problemTicketID);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_problemDescription);
            tvDateCreated = (TextView) itemView.findViewById(R.id.tv_problemDate);
        }

        void bind(final Problem problem, final OnItemClickListener listener) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(problem);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Problem problem);
    }
}
