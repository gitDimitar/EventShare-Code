package com.example.miteto.placer.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.miteto.placer.DTO.UserDTO;
import com.example.miteto.placer.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class LoginActivity extends FragmentActivity
{
    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS +1;
    private UserDTO userDTO;
    private boolean isResumed = false;
    private MenuItem settings;
    private boolean logout;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.miteto.eventsharealpha",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e)
        {

        }
        catch (NoSuchAlgorithmException e)
        {

        }

        setContentView(R.layout.activity_login);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++)
        {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        Intent i = getIntent();
        if(i.hasExtra("logout"))
        {
            String action = i.getStringExtra("logout");
            if(action.equals("logout"))
            {
                logout = true;
                i.removeExtra("logout");
            }
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        //sessionState changes determiner
        isResumed = true;
        uiHelper.onResume();
        if(logout)
        {
            //showLogoutFragment();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //sessionState changes determiner
        isResumed = false;
        uiHelper.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened())
        {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(SELECTION, false);
        }
        else
        {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // only add the menu when the selection fragment is showing
        if (fragments[SELECTION].isVisible())
        {
            if (menu.size() == 0)
            {
                settings = menu.add(R.string.settings);
            }
            return true;
        }
        else
        {
            menu.clear();
            settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.equals(settings))
        {
            showFragment(SETTINGS, true);
            return true;
        }
        return false;
    }

    //Showing selected fragment
    //Hiding other fragments
    private void showFragment(int fragmentIndex, boolean addToBackStack)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++)
        {
            if (i == fragmentIndex)
            {
                transaction.show(fragments[i]);
                logout = false;
            }
            else
            {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception)
    {
        // Only make changes if the activity is visible
        if (isResumed)
        {
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++)
            {
                manager.popBackStack();
            }
            if (state.isOpened())
            {
                // If the session state is open:
                // Show the authenticated fragment
                showFragment(SELECTION, false);
            }
            else if (state.isClosed())
            {
                // If the session state is closed:
                // Show the login fragment
                showFragment(SPLASH, false);
            }
        }
    }

    public void showLogoutFragment(View view)
    {
        showFragment(SETTINGS, true);
    }

    public void getLocation(View view)
    {
        Intent intent = new Intent(this,LocationActivity.class);
        intent.putExtra("user", userDTO);
        startActivity(intent);
    }

    public void setUserDTO(UserDTO userDTO)
    {
        this.userDTO = userDTO;
    }


}
