package de.ubuntudroid.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.ubuntudroid.fitnesstracker.view.base.BaseFragmentActivity;
import de.ubuntudroid.fitnesstracker.events.WeekSelectedEvent;


/**
 * An activity representing a list of Weeks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WeekDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WeekListFragment} and the item details
 * (if present) is a {@link WeekDetailFragment}.
 */
public class WeekListActivity extends BaseFragmentActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Inject
    Bus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list);

        mEventBus.register(this);

        if (findViewById(R.id.week_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WeekListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.week_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void itemSelected(WeekSelectedEvent event) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(WeekDetailFragment.ARG_ITEM_ID, event.getWeekId());
            WeekDetailFragment fragment = new WeekDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.week_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, WeekDetailActivity.class);
            detailIntent.putExtra(WeekDetailFragment.ARG_ITEM_ID, event.getWeekId());
            startActivity(detailIntent);
        }
    }
}