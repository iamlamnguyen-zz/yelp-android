package com.quiz.yelp.features;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.quiz.yelp.R;
import com.quiz.yelp.models.Restaurant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_DATA = "EXTRA_DATA";

    private Restaurant restaurant;

    @BindView(R.id.ivThumb)
    ImageView ivThumb;
    @BindView(R.id.tvName)
    EditText tvName;
    @BindView(R.id.tvPhone)
    EditText tvPhone;
    @BindView(R.id.tvCategories)
    EditText tvCategories;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.tvReview)
    EditText tvReview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void toActivity(Activity activity, Restaurant restaurant) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_DATA, restaurant);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        hasExtras(getIntent());

        toolbar.setTitle(restaurant.getName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        displayRestaurant(restaurant);
    }

    @SuppressLint("SetTextI18n")
    private void displayRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return;
        }
        Glide.with(this)
                .load(restaurant.getThumb())
                .into(ivThumb);
        tvName.setText(restaurant.getName());
        ratingBar.setRating((float) restaurant.getRating());
        tvPhone.setText(restaurant.getPhone());
        tvReview.setText(restaurant.getReview());

        if (restaurant.getTitleCategoryList() == null || restaurant.getTitleCategoryList().size() <= 0) {
            return;
        }
        String joinedCategory = TextUtils.join(" #", restaurant.getTitleCategoryList());
        tvCategories.setText("#" + joinedCategory);
    }

    private void hasExtras(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        restaurant = bundle.getParcelable(EXTRA_DATA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
