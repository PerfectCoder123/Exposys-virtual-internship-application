package com.example.exposysinternshipapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.exposysinternshipapp.Fragment.Applied;
import com.example.exposysinternshipapp.Fragment.Explore;
import com.example.exposysinternshipapp.Fragment.Home;
import com.example.exposysinternshipapp.Fragment.Profile;
import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {

   AnimatedBottomBar bottomAppBar;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         bottomAppBar = findViewById(R.id.bottom_bar);
         fragmentManager = getSupportFragmentManager();
         replaceFragment(new Home());
         applyTabListener();

    }

    private void applyTabListener(){
        bottomAppBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                switch (i1){
                    case 0 : replaceFragment(new Home());
                        break;
                    case 1 :replaceFragment(new Explore());
                        break;
                    case 2 : replaceFragment(new Applied());
                        break;
                    case 3 : replaceFragment(new Profile());
                        break;
                }
            }
            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }

    public void changeBottomBarIcon(Fragment fragment){
        if(fragment instanceof Home)          bottomAppBar.selectTabAt(0,false);
        else if(fragment instanceof Explore)  bottomAppBar.selectTabAt(1,false);
        else if(fragment instanceof Applied)  bottomAppBar.selectTabAt(2,false);
        else  bottomAppBar.selectTabAt(3,false);

    }

    public void replaceFragment(Fragment fragment){
        fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
        changeBottomBarIcon(fragment);
    }


}