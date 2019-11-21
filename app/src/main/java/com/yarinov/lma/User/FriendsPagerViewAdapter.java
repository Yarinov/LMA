package com.yarinov.lma.User;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yarinov.lma.User.Friends.AddFriendFragment;
import com.yarinov.lma.User.Friends.FriendRequestsFragment;
import com.yarinov.lma.User.Friends.MyFriendsFragment;

class FriendsPagerViewAdapter extends FragmentPagerAdapter {

    public FriendsPagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyFriendsFragment myFriendsFragment = new MyFriendsFragment();
                return myFriendsFragment;

            case 1:
                FriendRequestsFragment friendRequestsFragment = new FriendRequestsFragment();
                return friendRequestsFragment;

            case 2:
                AddFriendFragment addFriendFragment = new AddFriendFragment();
                return  addFriendFragment;

            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

}