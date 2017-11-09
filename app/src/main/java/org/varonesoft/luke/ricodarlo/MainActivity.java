package org.varonesoft.luke.ricodarlo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.database.OpenHelper;
import org.varonesoft.luke.ricodarlo.database.RicordaloProvider;
import org.varonesoft.luke.ricodarlo.database.models.Alert;

public class MainActivity extends AppCompatActivity implements
        AlertFragment.OnListFragmentInteractionListener{

    private static final java.lang.String TAG = "MainActivity";

    public static final int REQUESTCODE_EDIT = 1;
    public static final int REQUESTCODE_NEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                startActivityForResult(intent, REQUESTCODE_NEW);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            OpenHelper sqlDB = OpenHelper.getInstance(this);
            SQLiteDatabase db = sqlDB.getWritableDatabase();
            sqlDB.onCreate(db);

            Log.d(TAG, "Database recreated.");
            Toast.makeText(this, "Edit invocato ", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(String str) {
        // nothing now
    }

    @Override
    public void alertFragmentEdit(long id) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(Alert.STRINGKEY_ALERT_ID, String.valueOf(id));

        Log.d(TAG, String.format("alertFragmentEdit() [id=%s]", String.valueOf(id)));
        startActivityForResult(intent, REQUESTCODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Azione Annullata", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Result is NOT OK", Toast.LENGTH_SHORT).show();
            return;
        }

        switch ( requestCode) {
            case REQUESTCODE_EDIT:
                Toast.makeText(this, "Modificato con successo", Toast.LENGTH_SHORT).show();
                break;
            case REQUESTCODE_NEW:
                Toast.makeText(this, "Creato con successo", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Result OK", Toast.LENGTH_SHORT).show();;
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
