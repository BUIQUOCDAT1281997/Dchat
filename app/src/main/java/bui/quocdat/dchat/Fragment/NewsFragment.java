package bui.quocdat.dchat.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import bui.quocdat.dchat.Adapter.PostAdapter;
import bui.quocdat.dchat.Other.Post;
import bui.quocdat.dchat.Other.User;
import bui.quocdat.dchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private NavController navController;

    private List<Post> listData;
    private PostAdapter postAdapter;

    private RecyclerView recyclerView;
    private CircleImageView imageCurrentUser;

    private LinearLayout linearLayoutLoaderView;

    //FireBase
    private FirebaseUser firebaseUser;
    private DatabaseReference referenceFromPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_new, container, false);

        initViewAndFireBase(rootView);


        //load image to avatarUser
        DatabaseReference referenceFromUsers = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        referenceFromUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (!user.getAvatarURL().equals("default")) {
                    try {
                        Glide.with(getActivity().getApplicationContext())
                                .load(user.getAvatarURL())
                                .placeholder(R.drawable.ic_user)
                                .into(imageCurrentUser);
                    }catch (NullPointerException ignored){
                        Log.e("Error", ignored.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        readAllPost();

        return rootView;
    }

    private void initViewAndFireBase(View view) {
        linearLayoutLoaderView = view.findViewById(R.id.linearLayout_search);
        imageCurrentUser = view.findViewById(R.id.post_image_user);
        recyclerView = view.findViewById(R.id.recycler_view_all_post);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceFromPosts = FirebaseDatabase.getInstance().getReference("Posts");

        listData = new ArrayList<>();
    }

    private void readAllPost(){

        listData.clear();

        referenceFromPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    /*
                    if ((!post.getSenderID().equals(firebaseUser.getUid()))){
                        listData.add(post);
                    }
                     */
                    listData.add(post);
                }
                Collections.reverse(listData);
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutLoaderView.setVisibility(View.GONE);
                postAdapter = new PostAdapter(listData, getContext(), false);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.text_view_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_newsFragment_to_createNewPostFragment);
            }
        });
        view.findViewById(R.id.to_create_new_post_from_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("data", "here_we_go");
                navController.navigate(R.id.action_newsFragment_to_createNewPostFragment,bundle);
            }
        });
    }

}
