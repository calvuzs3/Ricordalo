package org.varonesoft.luke.ricodarlo;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.components.RicordaloIntentService;
import org.varonesoft.luke.ricodarlo.database.models.Alert;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AlertFragment extends ListFragment implements LoaderManager.LoaderCallbacks {

    // TAG
    private static final String TAG = AlertFragment.class.getSimpleName();

    // Parameter
    private static final String ARG_SEARCH_STRING = "search";

    // Members
    private static String mSearch;
    //private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;
    private OnListFragmentInteractionListener mListener;
    private AlertListAdapter mAlertListAdapter;

    // Contextual Menu in Action Mode
    private ActionMode.Callback mActionModeCallback;
    private Object mActionMode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlertFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlertFragment newInstance(String search) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_STRING, mSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSearch = getArguments().getString(ARG_SEARCH_STRING, "");
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAlertListAdapter = new AlertListAdapter(getActivity(), null);

//        mActionModeCallback = new ActionMode.Callback(){
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//
//                // Inflate menu resource
//                MenuInflater inflater = mode.getMenuInflater();
//                inflater.inflate(R.menu.list_menu, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//
//                switch (item.getItemId()){
//                    case R.id.ctx_menu_edit:
//                        // TODO invocare l'activity per l'editing
//                        mode.finish();
//                        Toast.makeText(getActivity(), "Edit invocato ", Toast.LENGTH_SHORT);
//                        return true;
//                    case R.id.ctx_menu_delete:
//                        // TODO delete
//                        mode.finish();
//                        Toast.makeText(getActivity(), "Delete invocato ", Toast.LENGTH_SHORT);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//                mActionMode = null;
//            }
//        };

        // Empty text
        setEmptyText(getString(R.string.str_no_alerts));

        // We have a menu
        setHasOptionsMenu(true);

        // A Context Menu
        registerForContextMenu(getListView());

//        getListView().setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                // Already CAB?
//                if (mActionMode != null) return false;
//
//                // Start the CAB with the callbacks defined above
//                mActionMode = getActivity().startActionMode(mActionModeCallback);
//                v.setSelected(true);
//                return true;
//            }
//        });
//        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mActionMode!=null) return;
//                Toast.makeText(getActivity(), "Clicked: "+id, Toast.LENGTH_SHORT);
//            }
//        });

        // Sets the adapter
        setListAdapter(mAlertListAdapter);

        //Starts out with a progress indicator
        setListShown(false);

        // Prepare the loader.
        // Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, AlertFragment.this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(getActivity(), "Clicked: "+id, Toast.LENGTH_SHORT);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        //menu.clear();
        super.onCreateContextMenu(menu, v, menuInfo);

        //final MenuInflater inflater = getActivity().getMenuInflater();
        //inflater.inflate(R.menu.list_menu, menu);
        menu.setHeaderIcon(android.R.drawable.btn_star);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, 1, 0, "Modifica");
        menu.add(0, 2, 0, "Elimina");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:

                // Perform a call to activity, say to edit the id selected
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo )item.getMenuInfo();
                int position = menuInfo.position;
                long id = menuInfo.id; // Here starts the problems..

                Log.d(TAG, String.format("onContextItemSelected() [id=%d]", id));
                mListener.alertFragmentEdit(id);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), Alert.URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        // Swap cursor
        mAlertListAdapter.swapCursor((Cursor )data);

        // The list should be shown
        if (isResumed()) setListShown(true);
        else setListShownNoAnimation(true);
    }


    @Override
    public void onLoaderReset(Loader loader) {

        mAlertListAdapter.swapCursor(null);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String str);

        void alertFragmentEdit(long id);
    }

    /**
     * Adattatore della lista
     */
    private class AlertListAdapter extends CursorAdapter {

        private Switch.OnCheckedChangeListener switchChange = new
                Switch.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        long alertID = Long.parseLong(buttonView.getTag().toString());
                        // Abilita ila field activate
                        toggleActivated(alertID);
                    }
                };

        public AlertListAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater li = LayoutInflater.from(context);
            View v = li.inflate(R.layout.fragment_alert_list_row, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //Alert a = new Alert(cursor);
            ((TextView) view.findViewById(R.id.list_name)).setText(cursor.getString(1));
            ((TextView) view.findViewById(R.id.list_desc)).setText(cursor.getString(2));

            Switch s = (Switch) view.findViewById(R.id.list_active);
            s.setOnCheckedChangeListener(null);
            s.setTag(String.valueOf(cursor.getLong(0)));
            s.setChecked(cursor.getInt(6) == Alert.STATUS_ACTIVATED);
            s.setOnCheckedChangeListener(switchChange);
        }
    }

    private void toggleActivated(long alertID) {

        // Get alert from db
        Alert alert = new Alert();

        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(Alert.URI, String.valueOf(alertID)),null, null, null, null);

        if ((cursor != null)) {
            if (cursor.moveToNext()){
                alert = new Alert(cursor);
            } else {
                Log.e(TAG, String.format("Unidentified Alert [id=%d]", alertID));
                return;
            }
            cursor.close();
        }

        alert.setStatus( alert.isActivated() ? Alert.STATUS_OFF: Alert.STATUS_ACTIVATED);
        alert.save(getActivity());

        if (alert.isActivated()) {

            //Set alarm through an Helper Method
            RicordaloIntentService.startActionSetAlarm( getActivity(), alert);
        } else {

            //Cancel alarm through an Helper Method
            RicordaloIntentService.startActionCancelAlarm( getActivity(), alert);
        }
    }
}
