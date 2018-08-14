package com.example.peak.newyoyo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peak.newyoyo.empty.ProductEmpty;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.peak.newyoyo.LoginActivity.mUser;

import static com.example.peak.newyoyo.adapterRecyclerView.AdapterRecyclerShowAll.KEY_EXTRA_OBJECT;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private ViewPager viewPager;
    private ArrayList<String> imgUrl = new ArrayList<>();
    private ProductEmpty empty;
    private ImageView imgUser, imgChat, imgCall;
    private TextView nameUser, namePro, pricePro, provinPro, descripPro;
    private Adapterpager adapterpager;
    private LinearLayout dotLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        findViewby();

        Intent itn = getIntent();
        empty = itn.getParcelableExtra(KEY_EXTRA_OBJECT);
        if (empty.getImage0_product() != null && !empty.getImage0_product().equals("null")) {
            imgUrl.add(empty.getImage0_product());
        }
        if (empty.getImage1_product() != null && !empty.getImage1_product().equals("null")) {
            imgUrl.add(empty.getImage1_product());
        }
        if (empty.getImage2_product() != null && !empty.getImage2_product().equals("null")) {
            imgUrl.add(empty.getImage2_product());
        }
        if (empty.getImage3_product() != null && !empty.getImage3_product().equals("null")) {
            imgUrl.add(empty.getImage3_product());
        }
        if (empty.getImage4_product() != null && !empty.getImage4_product().equals("null")) {
            imgUrl.add(empty.getImage4_product());
        }
        if (empty.getImage5_product() != null && !empty.getImage5_product().equals("null")) {
            imgUrl.add(empty.getImage5_product());
        }

        viewPager = findViewById(R.id.viewpager);
        adapterpager = new Adapterpager(this, imgUrl);
        viewPager.setAdapter(adapterpager);
        CreateDots(0);

        namePro.setText(empty.getName_product());
        nameUser.setText("ชื่อเจ้าของ : " + empty.getName_user());
        pricePro.setText("ราคา : " + empty.getPrice_product() + " ฿");
        provinPro.setText("จังหวัด : " + empty.getProvince_product());
        descripPro.setText("รายละเอียด : " + empty.getDescription_product());

        Picasso.with(this).load(empty.getUrl_imgprofile())
                .placeholder(R.drawable.ic_person_default)
                .into(imgUser);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CreateDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mId = mUser.getUid();
                final String idOther = empty.getUid();
                if (!mId.equals(idOther)) {
                    Intent itn = new Intent(DetailActivity.this, TalkActivity.class);
                    itn.putExtra("UID", empty.getUid());
                    itn.putExtra("name", empty.getName_user());
                    itn.putExtra("urlImage", empty.getUrl_imgprofile());
                    startActivity(itn);

                } else {
                    Toast.makeText(DetailActivity.this, "ไม่สามารถส่งข้อความได้เนื่องจากคุณเป็นเจ้าของสินค้านี้", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("โทร " + empty.getPhone_product())
                        .setMessage("ต้องการโทรหรือไม่")
                        .setCancelable(true)
                        .setPositiveButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("โทร", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + empty.getPhone_product()));

                                if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    startActivity(callIntent);
                                } else {
                                    requestPerms();
                                }
                            }
                        })
                        .create().show();

            }
        });
    }

    private void requestPerms() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(DetailActivity.this, perms, 0);
        } else {
            ActivityCompat.requestPermissions(DetailActivity.this, perms, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }

            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    private void CreateDots(int current_position) {
        if (dotLayout != null) {
            dotLayout.removeAllViews();
            dots = new ImageView[imgUrl.size()];
            for (int i = 0; i < imgUrl.size(); i++) {
                dots[i] = new ImageView(this);
                if (i == current_position) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ative_dot));
                } else {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.noactive_dot));
                }
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(4, 0, 4, 0);
                dotLayout.addView(dots[i], perams);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void findViewby() {
        imgUser = findViewById(R.id.img_profile_user);
        nameUser = findViewById(R.id.text_name_user);
        namePro = findViewById(R.id.text_namepro_detail);
        pricePro = findViewById(R.id.text_price_detail);
        provinPro = findViewById(R.id.text_province_detail);
        descripPro = findViewById(R.id.text_descrip_detail);
        dotLayout = findViewById(R.id.dot_layout);
        imgChat = findViewById(R.id.img_chat_detail);
        imgCall = findViewById(R.id.img_call_detail);
    }
}
