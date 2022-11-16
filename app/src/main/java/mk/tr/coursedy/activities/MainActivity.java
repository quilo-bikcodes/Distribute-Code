package mk.tr.coursedy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mk.tr.coursedy.R;
import mk.tr.coursedy.adapter.CourseAdapter;
import mk.tr.coursedy.course.CreateCourseDialog;
import mk.tr.coursedy.models.Course;
import mk.tr.coursedy.models.User;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private String codeText;
    DatabaseReference studentListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Remove title bar
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottom_nav);
        //Course Activity page select
        bottomNavigationView.setSelectedItemId(R.id.nav_courses);
        //Switch to other pages


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_courses);
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

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this,courseList);
        recyclerView.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();

        switchPage();

        addbtn();

        //courseAdapter.notifyDataSetChanged();
    }



    private void addbtn()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//").getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if(user!=null) {
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (user.getType().equals("Student")) {
                                openJoinCourseDialog();
                            } else if (user.getType().equals("Teacher")) {
                                openCourseDialog();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void openCourseDialog() {
        CreateCourseDialog createCourseDialog = new CreateCourseDialog();
        createCourseDialog.show(getSupportFragmentManager(),"Create Course");
    }

    private void readCourses() {

        DatabaseReference courseRef = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//").getReference("Courses");
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    final Course course =snapshot.getValue(Course.class);
                    if(course!=null)
                    {
                        final DatabaseReference studentRef = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//")
                                .getReference("StudentListOfTheCourses").child(course.getCourseId());
                        studentRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {


                                if(currentUser.getUid().equals(course.getCourseTeacherId())||
                                        dataSnapshot2.hasChild(currentUser.getUid()))
                                {
                                    courseList.add(course);
                                    courseAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
                courseAdapter.notifyDataSetChanged(); ////anında değişiklikleri yansıtsın diye.

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void openJoinCourseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Find out the class code from your teacher and enter it here.");
        final EditText codeInput = new EditText(MainActivity.this);
        codeInput.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(codeInput);
        dialog.setPositiveButton("OK", (dialog1, which) -> {

            codeText = codeInput.getText().toString();
            readCourseCodeFromDb(codeText);
        });
        dialog.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
        dialog.show();
    }
    private void readCourseCodeFromDb(final String codeInput)
    {

        final DatabaseReference reference = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//").getReference("Courses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int countIfCourseCode=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    final Course course = snapshot.getValue(Course.class);
                    assert course != null;

                    if(course.getCourseCode().equals(codeInput))
                    {
                        countIfCourseCode++;
                        studentListRef = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//").getReference("StudentListOfTheCourses")
                                .child(course.getCourseId());

                        DatabaseReference userRef = FirebaseDatabase.getInstance("https://coursedy-b1-default-rtdb.asia-southeast1.firebasedatabase.app//").getReference("Users")
                                .child(currentUser.getUid());

                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                User user = dataSnapshot2.getValue(User.class);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("studentId", currentUser.getUid());
                                assert user != null;
                                hashMap.put("studentName", user.getNameSurname());
                                hashMap.put("studentEmail", user.getEmail());
                                hashMap.put("studentPicture", user.getPictureUrl());
                                hashMap.put("courseId", course.getCourseId());

                                studentListRef.child(currentUser.getUid()).setValue(hashMap);
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }
                if(countIfCourseCode==0)
                    Toast.makeText(MainActivity.this, "Please enter a valid code.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }



    private void switchPage(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_courses:
                        return true;


                    case R.id.nav_assignments:
                        Intent intent = new Intent(getApplicationContext(),AssignmentsActivity.class);
                        //  intent.putExtra("courseId",course.getCourseId());
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_messages:
                        Intent intentMessage = new Intent(getApplicationContext(),MessagesActivity.class);
                        startActivity(intentMessage);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_announcements:
                        Intent intentAnnounce = new Intent(getApplicationContext(),AnnouncementsActivity.class);
                        startActivity(intentAnnounce);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_profile:
                        Intent intentProfile = new Intent(getApplicationContext(),ProfileActivity.class);
                        startActivity(intentProfile);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

}
