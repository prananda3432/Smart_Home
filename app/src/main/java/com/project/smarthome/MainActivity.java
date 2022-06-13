package com.project.smarthome;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Vibrator;

public class MainActivity extends AppCompatActivity {
    private SwitchCompat switch1, switch2, switch3, switch4;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private TextView fgs, time, date, temp, humi, read_voltage, read_current, read_power, read_energy;
    private ImageView fgi;
    private Vibrator v;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name1 = ("Channel 1");
            CharSequence name2 = ("Channel 2");
            String description1 = ("Channel for notification 1");
            String description2 = ("Channel for notification 2");
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel1 = new NotificationChannel("001", name1, importance);
            channel1.setDescription(description1);
            NotificationChannel channel2 = new NotificationChannel("002", name2, importance);
            channel2.setDescription(description2);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }

        int notificationId1 = 1;
        int notificationId2 = 2;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, "001")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Waspada")
                .setContentText("Terdeteksi asap pada ruangan")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "002")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Waspada")
                .setContentText("Terdeteksi kebocoran gas pada ruangan")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://logical-seat-314215-default-rtdb.asia-southeast1.firebasedatabase.app/");

        fgs = findViewById(R.id.home_fg_status);
        fgi = findViewById(R.id.fg_status);

        time = findViewById(R.id.time);
        date = findViewById(R.id.date);

        temp = findViewById(R.id.temp);
        humi = findViewById(R.id.humidity);

        read_voltage = findViewById(R.id.read_voltage);
        read_current = findViewById(R.id.read_current);
        read_power = findViewById(R.id.read_power);
        read_energy = findViewById(R.id.read_energy);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        database.getReference("MQ-2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Long smoke_status = (Long) dataSnapshot.child("Smoke").getValue();
                Long gas_status = (Long) dataSnapshot.child("Gas").getValue();

                if (dataSnapshot.exists()) {
                    if (smoke_status == 1) {
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.danger_blinking));
                        fgs.setText("SMOKE WAS DETECTED AT YOUR HOME!");
                        fgs.setTextColor(Color.RED);
                        notificationManager.notify(notificationId1, builder1.build());
                        v.vibrate(1000);
                    } else if (gas_status == 1) {
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.danger_blinking));
                        fgs.setText("GAS WAS DETECTED AT YOUR HOME!");
                        fgs.setTextColor(Color.RED);
                        notificationManager.notify(notificationId2, builder2.build());
                        v.vibrate(1000);
                    } else {
                        v.cancel();
                        fgi.setImageDrawable(getResources().getDrawable(R.drawable.safehome));
                        fgs.setText("YOUR HOME IS SAFE!");
                        fgs.setTextColor(getResources().getColor(R.color.textColor));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("NTP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String time_read = (String) dataSnapshot.child("Time").getValue();
                    String date_read = (String) dataSnapshot.child("Date").getValue();

                    time.setText(time_read + " WITA");
                    date.setText(date_read);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference("DHT-22").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String temperature = (String) dataSnapshot.child("Temperature").getValue();
                    String humidity = (String) dataSnapshot.child("Humidity").getValue();

                    temp.setText(temperature + " °C");
                    humi.setText(humidity + " %");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference("PZEM-004T").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String voltage = (String) dataSnapshot.child("Voltage").getValue();
                    String current = (String) dataSnapshot.child("Current").getValue();
                    String power = (String) dataSnapshot.child("Power").getValue();
                    String energy = (String) dataSnapshot.child("Energy").getValue();

                    read_voltage.setText(voltage);
                    read_current.setText(current);
                    read_power.setText(power);
                    read_energy.setText(energy);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference("Relay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String p1 = (String) dataSnapshot.child("Port_1").getValue();
                    String p2 = (String) dataSnapshot.child("Port_2").getValue();
                    String p3 = (String) dataSnapshot.child("Port_3").getValue();
                    String p4 = (String) dataSnapshot.child("Port_4").getValue();

                    if (p1.equals("ON")) {
                        imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch1.setChecked(true);
                    } else {
                        imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch1.setChecked(false);
                    }

                    if (p2.equals("ON")) {
                        imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch2.setChecked(true);
                    } else {
                        imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch2.setChecked(false);
                    }

                    if (p3.equals("ON")) {
                        imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch3.setChecked(true);
                    } else {
                        imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch3.setChecked(false);
                    }

                    if (p4.equals("ON")) {
                        imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                        switch4.setChecked(true);
                    } else {
                        imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                        switch4.setChecked(false);
                    }

                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch1.isChecked()) {
                                dataSnapshot.getRef().child("Port_1").setValue("ON");
                                imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                dataSnapshot.getRef().child("Port_1").setValue("OFF");
                                imageView1.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));

                            }
                        }
                    });

                    switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (switch2.isChecked()) {
                                dataSnapshot.getRef().child("Port_2").setValue("ON");
                                imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                dataSnapshot.getRef().child("Port_2").setValue("OFF");
                                imageView2.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });

                    switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch3.isChecked()) {
                                dataSnapshot.getRef().child("Port_3").setValue("ON");
                                imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                dataSnapshot.getRef().child("Port_3").setValue("OFF");
                                imageView3.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });

                    switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (switch4.isChecked()) {
                                dataSnapshot.getRef().child("Port_4").setValue("ON");
                                imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_on));
                            } else {
                                dataSnapshot.getRef().child("Port_4").setValue("OFF");
                                imageView4.setImageDrawable(getResources().getDrawable(R.mipmap.light_off));
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}