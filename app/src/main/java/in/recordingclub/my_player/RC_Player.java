package in.recordingclub.my_player;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.databinding.RcPlayerActivityBinding;


public class RC_Player extends AppCompatActivity implements View.OnClickListener {

private static final String tag = RC_Player.class.getName();
    private final int NOTIFICATION_ID = 1;
    private final String CHANNEL_ID = "rc_player_playbacks";
private      RcPlayerActivityBinding binding;
private      ExoPlayer player;
private boolean isPlaying = true, isFromFile = false, isBook;
    private int currentWindow = 0, current_position = 0;
    private long playbackPosition = 0, duration, fast_forward_increment = 10000, rewind_decrement = 10000;
    private PlaybackStateListener playbackStateListener;
    private String[] file, file_name, labels, content_durations;
    private String auto_play = "F";
    private float playback_speed = 1.0F;
    private MaterialButton btn_play_or_pause, btn_rewind, btn_fast_forward, btn_previous, btn_next;
private SharedPreferences sharedPreferences;
private SharedPreferences.Editor editor;
private MenuItem auto_play_menu, tableOfContentMenu, playBackSpeedMenu;
private AppInitializer initializer;
private GestureDetector gestureDetector;
private PlayerNotificationManager playerNotificationManager;
private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RcPlayerActivityBinding.inflate(getLayoutInflater());
        setTitle("RC Player");
        setContentView(binding.getRoot());
playbackStateListener = new PlaybackStateListener();
sharedPreferences = getPreferences(MODE_PRIVATE);
editor = sharedPreferences.edit();
initializer = new AppInitializer(getApplicationContext(), RC_Player.this);
gestureDetector = new GestureDetector(this, new GestureListener());
requestForBackgroundAccess();

try {
    Intent intent = getIntent();
    isBook = intent.getBooleanExtra("isBook", true);
} catch (Exception e) {
    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
}

try {
    rewind_decrement = getRewindDecrement();
    fast_forward_increment = getFastForwward();
} catch (Exception e) {
    rewind_decrement = 10000;
    fast_forward_increment = 10000;
}

    try {
        auto_play = sharedPreferences.getString("auto_play", "F");
    } catch (Exception e) {
        auto_play = "F";
    }

    try {
        playback_speed = Float.valueOf(getPlaybackSpeed());
    } catch (Exception e) {
        playback_speed = 1.0f;
    }

    btn_previous = findViewById(R.id.my_player_controller_btn_previous);
    btn_play_or_pause = findViewById(R.id.my_player_controller_btn_play_pause);
    btn_next = findViewById(R.id.my_player_controller_btn_next);
    btn_previous.setOnClickListener(this::onClick);
    btn_play_or_pause.setOnClickListener(this::onClick);
    btn_next.setOnClickListener(this::onClick);

    Intent data = getIntent();
        try {
            isFromFile = false;
            file = data.getStringArrayExtra("file");
            file_name = data.getStringArrayExtra("file_title");
            String book_name = data.getStringExtra("book");
                current_position = data.getIntExtra("current_position", 0);
            getChapterContents(file_name[current_position], book_name);
            binding.rcPlayerControlsTitleTv.setText(file_name[current_position]);
            initializePlayer(file[current_position]);
        } catch (Exception x) {
            isFromFile = true;
        }

        try {
    isFromFile = true;
String     tmp_data = data.getData().toString();
String uriPath = initializer.getAppFunctions().getPathFromUri(data.getData());
fileUri = data.getData();
            Toast.makeText(this, uriPath, Toast.LENGTH_SHORT).show();
File f = new File(uriPath);
current_position = 0;
file = new String[1];
file[0] = tmp_data;
btn_previous.setEnabled(false);
btn_next.setEnabled(false);
initializePlayer(file[current_position]);
binding.rcPlayerControlsTitleTv.setText(f.getName());

} catch (Exception e) {isFromFile = false;}

    onChange();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "RC Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rc_player_menus, menu);
        auto_play_menu = menu.findItem(R.id.rc_player_menu_auto_play);
        tableOfContentMenu = menu.findItem(R.id.chapter_contents);
        playBackSpeedMenu = menu.findItem(R.id.rc_player_menu_playback_speed);
        if (auto_play.equals("T")) {
            auto_play_menu.setTitle("Auto Play On");
        } else  {
            auto_play_menu.setTitle("Auto Play Off");
        }
        auto_play_menu.setVisible(!isFromFile);
        if (!isBook) {
            tableOfContentMenu.setVisible(false);
        } else if (isFromFile) {
            tableOfContentMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rc_player_menu_rewind) {
            setRewindDecrement();
        } else if (id == R.id.rc_player_menu_fast_forward) {
            setFastForwardIncrement();
        } else if (id == R.id.rc_player_menu_playback_speed) {
            setPlaybackSpeed();
        } else if (id == R.id.rc_player_menu_auto_play) {
            setAutoPlay();
        } else if (id == R.id.chapter_contents) {
showChapterContents();
        } else if (id == R.id.rc_player_menu_delete) {
            try {
                if (fileUri != null) {
                    getContentResolver().takePersistableUriPermission(
                            fileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            );
                    MediaDeletionHelper.deleteFile(this, fileUri);
                }

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                initializer.getAppFunctions().setCopyTextToClipboard(fileUri.toString());
            }

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.my_player_controller_btn_play_pause) {
            setPlayOrPause();
        } else if (id == R.id.my_player_controller_btn_previous) {
            setPrevious();
        } else if (id == R.id.my_player_controller_btn_next) {
            setNext();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
        if (! isFromFile) {
            editor.putString(file_name[current_position], String.valueOf(playbackPosition));
            editor.apply();
        }
        RC_Player.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MediaDeletionHelper.DELETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "File deleted successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete the file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestForBackgroundAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String  packageName = getPackageName();
            PowerManager powerManager =(PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                intent.setData(Uri.parse("package:" + packageName));
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                startActivity(intent);
            }
        }
    }

    private void showChapterContents() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setCancelable(true);
        builder.setTitle("Table of contents");
        LayoutInflater layoutInflater =(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chapter_contents_layout, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ListView listView = view.findViewById(R.id.contents_listview);
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), com.razorpay.R.layout.support_simple_spinner_dropdown_item, labels);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "No labels define for this chapter.", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long cDuration = Long.valueOf(content_durations[position]);
                player.seekTo(cDuration);
                alertDialog.dismiss();
            }
        });
    }

    private void initializePlayer(String file) {
        if (player == null) {
            if (! isFromFile) {
                boolean isBookMark = sharedPreferences.contains(file_name[current_position]);
                if (isBookMark) {
                    playbackPosition = Long.valueOf(sharedPreferences.getString(file_name[current_position], "00"));
                }
            }
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(getApplicationContext());
            player = new ExoPlayer.Builder(getApplicationContext())
                    .setTrackSelector(trackSelector)
                    .setSeekBackIncrementMs(rewind_decrement)
                    .setSeekForwardIncrementMs(fast_forward_increment)
                    .build();
String userAgent = Util.getUserAgent(getApplicationContext(), getPackageName());
            DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), userAgent);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(file));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(mediaItem);
            PlaybackParameters playbackParameters = new PlaybackParameters(playback_speed);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .build();
            player.setAudioAttributes(audioAttributes, true);
            player.setPlaybackParameters(playbackParameters);
            player.prepare(mediaSource);
            player.seekTo(playbackPosition);
            player.addListener(playbackStateListener);
            duration = player.getDuration();
            playbackPosition = player.getCurrentPosition();
            player.setPlayWhenReady(isPlaying);
            binding.rcPlayerView.setPlayer(player);
            binding.rcPlayerControlsView.setPlayer(player);
            btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
            btn_play_or_pause.setIconResource(R.drawable.pause_icon);
            try {
                playerNotificationManager = new PlayerNotificationManager.Builder(
                        this,
                        NOTIFICATION_ID,
                        CHANNEL_ID,
                        new PlayerNotificationManager.MediaDescriptionAdapter() {
                            @Override
                            public CharSequence getCurrentContentTitle(Player player) {
                                return "RC Player";
                            }

                            @Nullable
                            @Override
                            public PendingIntent createCurrentContentIntent(Player player) {
                                Intent intent = new Intent(getApplicationContext(), RC_Player.class);

                                return PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            }

                            @Nullable
                            @Override
                            public CharSequence getCurrentContentText(Player player) {
                                return "Recording Club";
                            }

                            @Nullable
                            @Override
                            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                                return BitmapFactory.decodeResource(getResources(), R.mipmap.rc_icon);
                            }
                        }
                ).build();
                playerNotificationManager.setPlayer(player);

                MediaSessionCompat mediaSession = new MediaSessionCompat(getApplicationContext(), "RCPlayer");
                mediaSession.setFlags(
                        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                );
                mediaSession.setActive(true);
                MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
mediaSessionConnector.setPlayer(player);
            } catch (Exception e) {
                Log.d(tag, e.getMessage());
            }
        }
    }

    private void releasePlayer() {
if (player != null) {
    playbackPosition = player.getCurrentPosition();
    currentWindow = player.getCurrentWindowIndex();
isPlaying = player.getPlayWhenReady();
player.removeListener(playbackStateListener);
player.release();
player = null;
btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_play));
btn_play_or_pause.setIconResource(R.drawable.play_icon);
}
    }

    private void setPlayOrPause() {
        if (btn_play_or_pause.getContentDescription().equals("Replay")) {
            player.seekTo(0);
            btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
            btn_play_or_pause.setIconResource(R.drawable.exo_icon_pause);

        } else {
            if (isPlaying) {
                isPlaying = !isPlaying;
                player.setPlayWhenReady(isPlaying);
                btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_play));
                btn_play_or_pause.setIconResource(R.drawable.exo_icon_play);
            } else {
                isPlaying = !isPlaying;
                player.setPlayWhenReady(isPlaying);
                btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                btn_play_or_pause.setIconResource(R.drawable.exo_icon_pause);
            }
        }
    }

    private void setPrevious() {
if (current_position > 0) {
    editor.putString(file_name[current_position], String.valueOf(playbackPosition));
    editor.apply();
    releasePlayer();
    playbackPosition = 0;
    currentWindow = 0;
    current_position--;
    playbackPosition = Long.valueOf(sharedPreferences.getString(file_name[current_position], "000"));
    initializePlayer(file[current_position]);
    binding.rcPlayerControlsTitleTv.setText(file_name[current_position]);
    onChange();
}
    }

    private void setNext() {
        if (current_position < file.length - 1) {
            editor.putString(file_name[current_position], String.valueOf(playbackPosition));
            editor.apply();
            releasePlayer();
            playbackPosition = 0;
            currentWindow = 0;
            current_position++;
            playbackPosition = Long.valueOf(sharedPreferences.getString(file_name[current_position], "000"));
            initializePlayer(file[current_position]);
            binding.rcPlayerControlsTitleTv.setText(file_name[current_position]);
            onChange();
        }
    }

    private void onChange() {
        int position = current_position + 1;
        if (position == 1) {
            btn_previous.setEnabled(false);
        } else if (position > 1) {
            btn_previous.setEnabled(true);
            btn_next.setEnabled(true);
            if (position == file.length) {
                btn_next.setEnabled(false);
            }
            if (position == 1 && position == file.length) {
                btn_previous.setEnabled(false);
                btn_next.setEnabled(false);
            }
        }
    }

    private void setAutoPlay() {
        if (auto_play.equals("F")) {
            auto_play = "T";
            auto_play_menu.setTitle("Auto Play On");
            editor.putString("auto_play", auto_play);
            editor.apply();
        } else if (auto_play.equals("T")){
            auto_play = "F";
            auto_play_menu.setTitle("Auto Play Off");
            editor.putString("auto_play", auto_play);
            editor.apply();
        }
    }

    private void setRewindDecrement() {
        releasePlayer();
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(RC_Player.this);
        dialogBuilder.setTitle("Select Rewind Decrement");
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.player_rewind_layout, null);
        dialogBuilder.setView(view);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(com.videvelopers.app.resources.R.color.background_color);
        CheckBox ch1 = view.findViewById(R.id.rw_ch1);
        CheckBox ch2 = view.findViewById(R.id.rw_ch2);
        CheckBox ch3 = view.findViewById(R.id.rw_ch3);
        CheckBox ch4 = view.findViewById(R.id.rw_ch4);
        CheckBox ch5 = view.findViewById(R.id.rw_ch5);

        if (rewind_decrement == 10000){
            ch1.setChecked(true);
        } else if (rewind_decrement == 30000){
            ch2.setChecked(true);
        } else if (rewind_decrement == 60000){
            ch3.setChecked(true);
        } else if (rewind_decrement == 120000){
            ch4.setChecked(true);
        } else if (rewind_decrement == 300000){
            ch5.setChecked(true);
        }

        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rewind_decrement = 10000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("rewind_decrement", rewind_decrement);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rewind_decrement = 30000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("rewind_decrement", rewind_decrement);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rewind_decrement = 60000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("rewind_decrement", rewind_decrement);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rewind_decrement = 120000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("rewind_decrement", rewind_decrement);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rewind_decrement = 300000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("rewind_decrement", rewind_decrement);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isPlaying = true;
                initializePlayer(file[current_position]);
                player.seekTo(playbackPosition);
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isPlaying = true;
                initializePlayer(file[current_position]);
                player.seekTo(playbackPosition);
            }
        });
    }

private void setFastForwardIncrement() {
        releasePlayer();
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(RC_Player.this);
        dialogBuilder.setTitle("Select Fast Forward Increment");
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.player_fast_forward_layout, null);
        dialogBuilder.setView(view);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(com.videvelopers.app.resources.R.color.background_color);

        CheckBox ch1 = view.findViewById(R.id.fs_ch1);
        CheckBox ch2 = view.findViewById(R.id.fs_ch2);
        CheckBox ch3 = view.findViewById(R.id.fs_ch3);
        CheckBox ch4 = view.findViewById(R.id.fs_ch4);
        CheckBox ch5 = view.findViewById(R.id.fs_ch5);
        if (fast_forward_increment == 10000){
            ch1.setChecked(true);
        } else  if (fast_forward_increment == 30000){
            ch2.setChecked(true);
        } else  if (fast_forward_increment == 60000){
            ch3.setChecked(true);
        } else  if (fast_forward_increment == 120000){
            ch4.setChecked(true);
        } else  if (fast_forward_increment == 300000){
            ch5.setChecked(true);
        }

        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fast_forward_increment = 10000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("fast_forward_increment", fast_forward_increment);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fast_forward_increment = 30000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("fast_forward_increment", fast_forward_increment);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fast_forward_increment = 60000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("fast_forward_increment", fast_forward_increment);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fast_forward_increment = 120000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("fast_forward_increment", fast_forward_increment);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        ch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fast_forward_increment = 300000;
                    isPlaying = true;
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putLong("fast_forward_increment", fast_forward_increment);
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isPlaying = true;
                initializePlayer(file[current_position]);
                player.seekTo(playbackPosition);
            }
        });

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isPlaying = true;
                initializePlayer(file[current_position]);
                player.seekTo(playbackPosition);
            }
        });
    }

    private void setPlaybackSpeed() {
        releasePlayer();
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(RC_Player.this);
        dialogBuilder.setTitle("Select Playback Speed");
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.playback_speed_dialog, null);
        dialogBuilder.setView(view);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(com.videvelopers.app.resources.R.color.background_color);

        RadioButton speed_0_5x = view.findViewById(R.id.speed_0_5x);
        RadioButton speed_0_75x = view.findViewById(R.id.speed_0_7x);
        RadioButton speed_1_0x = view.findViewById(R.id.speed_1_0x);
        RadioButton speed_1_25x =                 view.findViewById(R.id.speed_1_25x);
        RadioButton speed_1_5x = view.findViewById(R.id.speed_1_5x);
        RadioButton speed_1_75x = view.findViewById(R.id.speed_1_75x);
        RadioButton speed_2_0x = view.findViewById(R.id.speed_2_0x);
        RadioButton speed_2_25x = view.findViewById(R.id.speed_2_25x);
        RadioButton speed_2_5x = view.findViewById(R.id.speed_2_5x);
        RadioButton speed_2_75x = view.findViewById(R.id.speed_2_75x);
        RadioButton speed_3_0x = view.findViewById(R.id.speed_3_0x);

        if (playback_speed == 0.5) {
            speed_0_5x.setChecked(true);
        } else if (playback_speed == 0.75) {
            speed_0_75x.setChecked(true);
        } else if (playback_speed == 1.0) {
            speed_1_0x.setChecked(true);
        } else if (playback_speed == 1.25) {
            speed_1_25x.setChecked(true);
        } else if (playback_speed == 1.5) {
            speed_1_5x.setChecked(true);
        } else if (playback_speed == 1.75) {
            speed_1_75x.setChecked(true);
        } else if (playback_speed == 2.0) {
            speed_2_0x.setChecked(true);
        } else if (playback_speed == 2.25) {
            speed_2_25x.setChecked(true);
        } else if (playback_speed == 2.5) {
            speed_2_5x.setChecked(true);
        } else if (playback_speed == 2.75) {
            speed_2_75x.setChecked(true);
        } else if (playback_speed == 3.0) {
            speed_3_0x.setChecked(true);
        }

        speed_0_5x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    playback_speed = 0.5F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_0_75x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 0.75F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_1_0x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 1.0F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_1_25x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 1.25F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_1_5x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 1.5F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_1_75x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 1.75F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_2_0x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 2.0F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_2_25x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 2.25F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_2_5x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 2.5F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_2_75x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 2.75F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

        speed_3_0x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    releasePlayer();
                    playback_speed = 3.0F;
                    isPlaying = true;
                    btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
                    btn_play_or_pause.setIconResource(R.drawable.pause_icon);
                    initializePlayer(file[current_position]);
                    player.seekTo(playbackPosition);
                    editor.putString("playback_speed", String.valueOf(playback_speed));
                    editor.apply();
                    alertDialog.dismiss();
                }
            }
        });

alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
    @Override
    public void onDismiss(DialogInterface dialog) {
        isPlaying = true;
        btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
        btn_play_or_pause.setIconResource(R.drawable.pause_icon);
        initializePlayer(file[current_position]);
        player.seekTo(playbackPosition);
    }
});

alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialog) {
        isPlaying = true;
        btn_play_or_pause.setContentDescription(getResources().getString(R.string.btn_pause));
        btn_play_or_pause.setIconResource(R.drawable.pause_icon);
        initializePlayer(file[current_position]);
        player.seekTo(playbackPosition);
    }
});

    }

    private String getAutoPlay() {
        String tmp_auto_play_value = sharedPreferences.getString("auto_play", "F");
        return tmp_auto_play_value;
    }

    private long getRewindDecrement() {
        return sharedPreferences.getLong("rewind_decrement", 10000);
    }

    private long getFastForwward() {
        return sharedPreferences.getLong("fast_forward_increment", 10000);
    }

    private String getPlaybackSpeed() {
        return sharedPreferences.getString("playback_speed", "1.0");
    }

    private void getChapterContents(String chapter_title, String book_title) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().get_chapter_labels, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("labels");
                    JSONArray jsonArray2 = jsonObject.getJSONArray("durations");
                    labels = new String[jsonArray1.length()];
                    content_durations = new String[jsonArray2.length()];
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        labels[i] = jsonArray1.getString(i);
                        content_durations[i] = jsonArray2.getString(i);
                    }
                    //Toast.makeText(RC_Player.this, labels[0], Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(RC_Player.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(RC_Player.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("chapter", chapter_title);
                params.put("book", book_title);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private class PlaybackStateListener implements Player.Listener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            binding.rcPlayerView.showController();
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";

                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    if (auto_play.equals("T")) {
                        setNext();
                    } else {
                        btn_play_or_pause.setContentDescription(getString(R.string.btn_replay));
                        btn_play_or_pause.setIconResource(R.drawable.replay);
                    }
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
        }
    }

private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                setPrevious();
            } else {
                setNext();
            }
            return true;
        }
        return false;
    }
}
    }