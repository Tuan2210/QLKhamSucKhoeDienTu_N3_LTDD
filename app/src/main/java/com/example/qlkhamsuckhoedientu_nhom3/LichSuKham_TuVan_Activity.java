package com.example.qlkhamsuckhoedientu_nhom3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class LichSuKham_TuVan_Activity extends AppCompatActivity {
    private ThongTinChung thongTinChung;
    private ThongTinLSKham_TuVan thongTinLSKham_tuVan;
    private ThongTinCaNhan thongTinCaNhan;

    private ArrayList<String> arrayList=new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<ThongTinChung> thongTinChungArrayList=new ArrayList<ThongTinChung>();

    private DatabaseReference ref;
    private SQLiteDBHandler db;

    private Date ngayHT, gioHT;
    private SimpleDateFormat sdfNgayHT=new SimpleDateFormat("dd-MM-yyyy"), sdfGioHT =new SimpleDateFormat("HH:mm:ss a");
    private LocalDate now;
    int ngay, thang, nam, gio, phut, giay;

    ImageView imgBack_LSKham;
    EditText edtBHYT, edtBenhVien, edtKhoa, edtChuanDoan, edtEmailDN;
    Button btnLuu, btnXoa, btnCapNhatLSK, btnHuy;

    TextView tvInfoLSK;
    ListView lvLSKham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lichsukham_tuvan);

        edtEmailDN = findViewById(R.id.edtEmailDN);

        imgBack_LSKham = findViewById(R.id.imgBack_LSKham);
        tvInfoLSK = findViewById(R.id.tvInfoLSK);
        edtBHYT = findViewById(R.id.edtBHYT);
        edtBenhVien = findViewById(R.id.edtBenhVien);
        edtKhoa = findViewById(R.id.edtKhoa);
        edtChuanDoan = findViewById(R.id.edtChuanDoan);
        btnLuu = findViewById(R.id.btnLuu);
        btnXoa = findViewById(R.id.btnXoa);
        btnCapNhatLSK = findViewById(R.id.btnCapNhatLSK);
        btnHuy = findViewById(R.id.btnHuy);
        lvLSKham = findViewById(R.id.lvLSKham);

        db = new SQLiteDBHandler(getApplicationContext());
//        thongTinChungArrayList = (ArrayList<ThongTinChung>) db.getAllPatientsInfo();

        showDataLV(); //show LV; h??m chooseInfoDeleteOrUpdate(arrayList) x??? l?? delete, update

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });

        imgBack_LSKham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LichSuKham_TuVan_Activity.this.startActivity(new Intent(LichSuKham_TuVan_Activity.this, NguoiDung_Activity.class));
            }
        });
    }

    public void showDataLV() {
        ref = FirebaseDatabase.getInstance().getReference("B???nh nh??n")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("L???ch s??? kh??m - T?? v???n");

        arrayAdapter=new ArrayAdapter<String>(this, R.layout.item_lichsukham, R.id.tvInfoLSK, arrayList);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ngayKham = dataSnapshot.getKey();
                    thongTinLSKham_tuVan = dataSnapshot.getValue(ThongTinLSKham_TuVan.class);
                    String bhyt = "" + thongTinLSKham_tuVan.getBHYT(),
                            bv = "" + thongTinLSKham_tuVan.getBenhVien(),
                            khoa = "" + thongTinLSKham_tuVan.getKhoa(),
                            chd = "" + thongTinLSKham_tuVan.getChuanDoan();

                    String infoLSK = ""+ngayKham
                            +"\nBHYT: " +bhyt +" \t\t\tB???nh vi???n: " +bv +"   "
                            +"\nKhoa: " +khoa +"     "
                            +"\nChu???n ??o??n: " +chd +"    ";
                    arrayList.removeAll(Collections.singleton(infoLSK));
                    arrayList.add(infoLSK);

                    //java.util.Collections.reverse(arrayList); //sort descending; firebase auto ascending

                    String checkLSK = "" +infoLSK;
                    Log.d("CHECK LSK: ", checkLSK);

                    chooseInfoDeleteOrUpdate(arrayList);
                }
                lvLSKham.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkInputs() {
        String txtBHYT = edtBHYT.getText().toString().trim(),
                txtBV = edtBenhVien.getText().toString().trim(),
                txtKhoa = edtKhoa.getText().toString().trim(),
                txtChuanDoan = edtChuanDoan.getText().toString().trim();

        //ngay,thang,nam hien tai
        now = LocalDate.now();
        ngay = now.getDayOfMonth();
        thang = now.getMonthValue();
        nam = now.getYear();
        ngayHT = new Date(nam - 1900, thang - 1, ngay);

        //gio,phut,giay hien tai
        gioHT = new Date();
        gio = gioHT.getHours();
        phut = gioHT.getMinutes();
        giay = gioHT.getSeconds();

        String ngayHienTai = sdfNgayHT.format(ngayHT),
                gioHienTai = sdfGioHT.format(gioHT),
                ngayGioHT = "" + ngayHienTai + ", " + gioHienTai;

        if (txtBHYT.equals("") || txtBV.equals("") || txtKhoa.equals("") || txtChuanDoan.equals(""))
            Toast.makeText(this, "Vui l??ng nh???p v??o ch??? tr???ng", Toast.LENGTH_SHORT).show();
        else {
            //load username
            String ten= getIntent().getStringExtra("tenNguoiDung");

            writeData(ten, ngayGioHT, txtBHYT, txtChuanDoan, txtBV, txtKhoa);
        }
    }

    public void writeData(String ten, String ngayGioHT, String txtBHYT, String txtChuanDoan, String txtBV, String txtKhoa){
        //realtimeDB
        thongTinLSKham_tuVan = new ThongTinLSKham_TuVan(txtBHYT, txtChuanDoan, txtBV, txtKhoa);
        ref = FirebaseDatabase.getInstance().getReference("B???nh nh??n");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("L???ch s??? kh??m - T?? v???n")
                .child("Ng??y kh??m: " +ngayGioHT).setValue(thongTinLSKham_tuVan);

        //sqliteDB
        thongTinCaNhan = new ThongTinCaNhan(ten);
        ThongTinLSKham_TuVan allInfoLSK = new ThongTinLSKham_TuVan(ngayGioHT, txtBHYT, txtChuanDoan, txtBV, txtKhoa);
        thongTinChung = new ThongTinChung(thongTinCaNhan, allInfoLSK);
        db.addPatientsInfo(thongTinChung);

        Toast.makeText(this, "L??u th??nh c??ng", Toast.LENGTH_SHORT).show();
        resetAll();

    }

    public void chooseInfoDeleteOrUpdate(ArrayList arrayList){
        lvLSKham.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ArrayList<String> arrayList1 = new ArrayList<>();
//                String getInfo = arrayList1.get(i);
                String getInfo = adapterView.getItemAtPosition(i).toString();

                //cop ??o???n chu???i t??? getInfo qua getNgayKham; b???t ?????u t??? 0, d???ng ??? k?? t??? 33 (ch??? M trong AM ho???c PM), 34 ko l???y
                String getNgayKham = getInfo.substring(0, 34),
                        getNgayKhamSQLite = getInfo.substring(11, 34);

                ref = FirebaseDatabase.getInstance().getReference("B???nh nh??n")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("L???ch s??? kh??m - T?? v???n");

//                edtBHYT.setText(getInfo.substring(41, 46)); //41 l?? k?? t??? C; K //ok
//                edtBenhVien.setText(getInfo.substring(61, 67)); //50 l?? k?? t??? B, 67 \n
//                edtKhoa.setText(getInfo.substring(74, 85)); //68 l?? k?? t??? X (X??t nghi???m), 85 \n
//                edtChuanDoan.setText(getInfo.substring(98, 111)); //86 l?? k?? t??? C, 96 :,

//                if(arrayList.contains("")==true)
//                    Toast.makeText(LichSuKham_TuVan_Activity.this, "Ch??a ch???n th??ng tin l???ch s??? ????? th???c hi???n c??c ch???c n??ng", Toast.LENGTH_SHORT).show();

                if(arrayList.contains(getInfo) == true){ //check getInfo c?? trong arrayList hay ko
                    //x??a
                    btnXoa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //realtimeDB
                            ref.child(getNgayKham).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //sqliteDB
                                        thongTinChung=new ThongTinChung(new ThongTinLSKham_TuVan(getNgayKhamSQLite));
                                        db.deletePatientsInfo(thongTinChung);

                                        Toast.makeText(LichSuKham_TuVan_Activity.this, "X??a th??nh c??ng", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            arrayList.remove(getInfo);
                            resetAll();
                        }
                    });

                    //c???p nh???t
                    btnCapNhatLSK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String txtBHYT = edtBHYT.getText().toString().trim(),
                                    txtBV = edtBenhVien.getText().toString().trim(),
                                    txtKhoa = edtKhoa.getText().toString().trim(),
                                    txtChuanDoan = edtChuanDoan.getText().toString().trim();

                            if (txtBHYT.equals("") || txtBV.equals("") || txtKhoa.equals("") || txtChuanDoan.equals(""))
                                Toast.makeText(LichSuKham_TuVan_Activity.this, "Vui l??ng nh???p v??o ch??? tr???ng", Toast.LENGTH_SHORT).show();
                            else {
                                thongTinLSKham_tuVan = new ThongTinLSKham_TuVan(txtBHYT, txtChuanDoan, txtBV, txtKhoa);

                                //realtimeDB
                                ref = FirebaseDatabase.getInstance().getReference("B???nh nh??n");
                                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("L???ch s??? kh??m - T?? v???n")
                                        .child(getNgayKham).setValue(thongTinLSKham_tuVan);

                                //sqliteDB
                                ThongTinLSKham_TuVan detailLsKhamTuVan = new ThongTinLSKham_TuVan(getNgayKhamSQLite, txtBHYT, txtChuanDoan, txtBV, txtKhoa);
                                thongTinChung = new ThongTinChung(detailLsKhamTuVan);
                                db.updatePatientsInfo(thongTinChung);

                                Toast.makeText(LichSuKham_TuVan_Activity.this, "C???p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                                resetAll();

                                //reload activity
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    });

                    String log = "" + getInfo;
                    Log.d("check get 1 info: ", log);
                    Toast.makeText(LichSuKham_TuVan_Activity.this, ""+getInfo, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void resetAll(){
        edtBHYT.requestFocus();
        edtBHYT.setText("");
        edtBenhVien.setText("");
        edtKhoa.setText("");
        edtChuanDoan.setText("");
    }

}