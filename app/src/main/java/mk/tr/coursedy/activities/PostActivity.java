package mk.tr.coursedy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mk.tr.coursedy.R;
import mk.tr.coursedy.adapter.PostAdapter;
import mk.tr.coursedy.models.Post;

public class PostActivity extends AppCompatActivity {

    private String courseId, courseName, courseTeacherName, coursePeriod;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_post);


        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        courseName = intent.getStringExtra("courseName");
        coursePeriod = intent.getStringExtra("coursePeriod");
        courseTeacherName = intent.getStringExtra("courseTeacherName");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        assert verticalDivider != null;
        dividerItemDecoration.setDrawable(verticalDivider);
        recyclerView.addItemDecoration(dividerItemDecoration);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this,postList);
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        readPosts();


    }

    private void readPosts() {

        DatabaseReference refAssignment = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//")
                .getReference("Posts").child(courseId);
        refAssignment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
