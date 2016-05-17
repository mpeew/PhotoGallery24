package com.mpewpazi.android.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mpewpazi on 3/29/16.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //pake support karena kita pake library support
        //buat toolnya
        FragmentManager fm = getSupportFragmentManager();

        //cari fragment yang idnya berikut
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        //kalau gak ada buat fragment baru
        if (fragment == null) {
            fragment = createFragment();
            //masukan kedalam list transaksi
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();

            //fragment manager punya tugas untuk memanggil lifecycle fragment didalam listnya
            //ketika fragment dimasukan kedalam list maka fragment lifecycle pun jalan

        }
    }
}

//rule of thumb jangan pake fragment lebih dari 3 dalam 1 layar
