package com.naresh.kingupadhyay.mathsking.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.MainActivity;
import com.naresh.kingupadhyay.mathsking.activities.Concepts;
import com.naresh.kingupadhyay.mathsking.activities.ReviewTest;

import java.util.HashMap;
import java.util.Map;

public class Performance extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_performance, container, false);
        defaultSettings(viewFragment);
        savedScoreSettings(viewFragment);
        Button solutionButton = viewFragment.findViewById(R.id.solutionButton);
        solutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReviewTest.class);
                startActivity(intent);
            }
        });
        //todo increase credit twice when it came from live/same if from previous
        return viewFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void savedScoreSettings(View view){
        TextView scoredval = view.findViewById(R.id.scoreval);
        TextView correctscoreval = view.findViewById(R.id.correctscoreval);
        TextView wrongscoreval = view.findViewById(R.id.wrongscoreval);
        SharedPreferences prefLive = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        boolean liveCheck = prefLive.getBoolean("live",false);
        String liveid = prefLive.getString("liveId","liveId");

        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        int solutionMade = prefUseId.getInt("solutionMade",0);
        String currentUserId=prefUseId.getString("uid","userkhk");

        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
        final int total = prefs.getInt(liveid+"liveScoreTotal",0);
        final int markspos = prefs.getInt(liveid+"liveScorepos",0);
        final int marksneg = prefs.getInt(liveid+"liveScoreneg",0);


        if(liveCheck){
            solutionMade = solutionMade + (int)(total/2);
        }else{
            solutionMade = solutionMade + (int)(total/4);
        }
        if(solutionMade<0){
            solutionMade = 0;
        }
        SharedPreferences.Editor edt = prefUseId.edit();
        edt.putInt("solutionMade",solutionMade);
        edt.putInt("rating", PDFTools.ratingCalculation(solutionMade));
        edt.apply();

        try{
            final FirebaseFirestore db= FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("users").document(currentUserId);
            Map<String,Object> uploadMap = new HashMap<>();
            uploadMap.put("solved",solutionMade);
            docRef.set(uploadMap, SetOptions.merge());

        }catch (Exception e){
        }


        try{
            scoredval.setText(""+total);
            correctscoreval.setText(""+markspos);
            wrongscoreval.setText(""+marksneg);
        }catch (Exception e){
        }

        //todo good at
        TextView t0 = view.findViewById(R.id.determinatspercent);
        TextView t1 = view.findViewById(R.id.matricespercent);
        TextView t2 = view.findViewById(R.id.logrithmicpercent);
        TextView t3 = view.findViewById(R.id.seriespercent);
        TextView t4 = view.findViewById(R.id.equationpercent);
        TextView t5 = view.findViewById(R.id.complexpercent);
        TextView t6 = view.findViewById(R.id.pncpercent);
        TextView t7 = view.findViewById(R.id.binomialpercent);
        TextView t8 = view.findViewById(R.id.probabilitypercent);
        TextView t9 = view.findViewById(R.id.functionpercent);
        TextView t10 = view.findViewById(R.id.limitpercent);
        TextView t11 = view.findViewById(R.id.continutypercent);
        TextView t12 = view.findViewById(R.id.differentialbilitypercent);
        TextView t13 = view.findViewById(R.id.differentialtionpercent);
        TextView t14 = view.findViewById(R.id.monotonicitypercent);
        TextView t15 = view.findViewById(R.id.max_minpercent);
        TextView t16 = view.findViewById(R.id.tang_normalpercent);
        TextView t17 = view.findViewById(R.id.ratepercent);
        TextView t18 = view.findViewById(R.id.rolleslangpercent);
        TextView t19 = view.findViewById(R.id.indefinitepercent);
        TextView t20 = view.findViewById(R.id.definitepercent);
        TextView t21 = view.findViewById(R.id.areapercent);
        TextView t22 = view.findViewById(R.id.differeneqpercent);
        TextView t23 = view.findViewById(R.id.ratio_identitypercent);
        TextView t24 = view.findViewById(R.id.trignoeqpercent);
        TextView t25 = view.findViewById(R.id.inverse_trigopercent);
        TextView t26 = view.findViewById(R.id.height_distpercent);
        TextView t27 = view.findViewById(R.id.straight_linepercent);
        TextView t28 = view.findViewById(R.id.pair_linepercent);
        TextView t29 = view.findViewById(R.id.circlepercent);
        TextView t30 = view.findViewById(R.id.parabolapercent);
        TextView t31 = view.findViewById(R.id.ellipsepercent);
        TextView t32 = view.findViewById(R.id.hyperbolapercent);
        TextView t33 = view.findViewById(R.id.three_dpercent);
        TextView t34 = view.findViewById(R.id.vectorpercent);

        LinearLayout p0 = view.findViewById(R.id.p0);
        LinearLayout p1 = view.findViewById(R.id.p1);
        LinearLayout p2 = view.findViewById(R.id.p2);
        LinearLayout p3 = view.findViewById(R.id.p3);
        LinearLayout p4 = view.findViewById(R.id.p4);
        LinearLayout p5 = view.findViewById(R.id.p5);
        LinearLayout p6 = view.findViewById(R.id.p6);
        LinearLayout p7 = view.findViewById(R.id.p7);
        LinearLayout p8 = view.findViewById(R.id.p8);
        LinearLayout p9 = view.findViewById(R.id.p9);
        LinearLayout p10 = view.findViewById(R.id.p10);
        LinearLayout p11 = view.findViewById(R.id.p11);
        LinearLayout p12 = view.findViewById(R.id.p12);
        LinearLayout p13 = view.findViewById(R.id.p13);
        LinearLayout p14 = view.findViewById(R.id.p14);
        LinearLayout p15 = view.findViewById(R.id.p15);
        LinearLayout p16 = view.findViewById(R.id.p16);
        LinearLayout p17 = view.findViewById(R.id.p17);
        LinearLayout p18 = view.findViewById(R.id.p18);
        LinearLayout p19 = view.findViewById(R.id.p19);
        LinearLayout p20 = view.findViewById(R.id.p20);
        LinearLayout p21 = view.findViewById(R.id.p21);
        LinearLayout p22 = view.findViewById(R.id.p22);
        LinearLayout p23 = view.findViewById(R.id.p23);
        LinearLayout p24 = view.findViewById(R.id.p24);
        LinearLayout p25 = view.findViewById(R.id.p25);
        LinearLayout p26 = view.findViewById(R.id.p26);
        LinearLayout p27 = view.findViewById(R.id.p27);
        LinearLayout p28 = view.findViewById(R.id.p28);
        LinearLayout p29 = view.findViewById(R.id.p29);
        LinearLayout p30 = view.findViewById(R.id.p30);
        LinearLayout p31 = view.findViewById(R.id.p31);
        LinearLayout p32 = view.findViewById(R.id.p32);
        LinearLayout p33 = view.findViewById(R.id.p33);
        LinearLayout p34 = view.findViewById(R.id.p34);


        int percent0 = prefs.getInt(liveid+0,0);
        int percent1 = prefs.getInt(liveid+1,0);
        int percent2 = prefs.getInt(liveid+2,0);
        int percent3 = prefs.getInt(liveid+3,0);
        int percent4 = prefs.getInt(liveid+4,0);
        int percent5 = prefs.getInt(liveid+5,0);
        int percent6 = prefs.getInt(liveid+6,0);
        int percent7 = prefs.getInt(liveid+7,0);
        int percent8 = prefs.getInt(liveid+8,0);
        int percent9 = prefs.getInt(liveid+9,0);
        int percent10 = prefs.getInt(liveid+10,0);
        int percent11 = prefs.getInt(liveid+11,0);
        int percent12 = prefs.getInt(liveid+12,0);
        int percent13 = prefs.getInt(liveid+13,0);
        int percent14 = prefs.getInt(liveid+14,0);
        int percent15 = prefs.getInt(liveid+15,0);
        int percent16 = prefs.getInt(liveid+16,0);
        int percent17 = prefs.getInt(liveid+17,0);
        int percent18 = prefs.getInt(liveid+18,0);
        int percent19 = prefs.getInt(liveid+19,0);
        int percent20 = prefs.getInt(liveid+20,0);
        int percent21 = prefs.getInt(liveid+21,0);
        int percent22 = prefs.getInt(liveid+22,0);
        int percent23 = prefs.getInt(liveid+23,0);
        int percent24 = prefs.getInt(liveid+24,0);
        int percent25 = prefs.getInt(liveid+25,0);
        int percent26 = prefs.getInt(liveid+26,0);
        int percent27 = prefs.getInt(liveid+27,0);
        int percent28 = prefs.getInt(liveid+28,0);
        int percent29 = prefs.getInt(liveid+29,0);
        int percent30 = prefs.getInt(liveid+30,0);
        int percent31 = prefs.getInt(liveid+31,0);
        int percent32 = prefs.getInt(liveid+32,0);
        int percent33 = prefs.getInt(liveid+33,0);
        int percent34 = prefs.getInt(liveid+34,0);

        if(percent0>=50){
            p0.setVisibility(View.VISIBLE);
            t0.setText(""+percent0);
        }else{
            p0.setVisibility(View.GONE);
        }
        if(percent1>=50){
            p1.setVisibility(View.VISIBLE);
            t1.setText(""+percent1);
        }else{
            p1.setVisibility(View.GONE);
        }
        if(percent2>=50){
            p2.setVisibility(View.VISIBLE);
            t2.setText(""+percent2);
        }else{
            p2.setVisibility(View.GONE);
        }
        if(percent3>=50){
            p3.setVisibility(View.VISIBLE);
            t3.setText(""+percent3);
        }else{
            p3.setVisibility(View.GONE);
        }
        if(percent4>=50){
            p4.setVisibility(View.VISIBLE);
            t4.setText(""+percent4);
        }else{
            p4.setVisibility(View.GONE);
        }
        if(percent5>=50){
            p5.setVisibility(View.VISIBLE);
            t5.setText(""+percent5);
        }else{
            p5.setVisibility(View.GONE);
        }
        if(percent6>=50){
            p6.setVisibility(View.VISIBLE);
            t6.setText(""+percent6);
        }else{
            p6.setVisibility(View.GONE);
        }
        if(percent7>=50){
            p7.setVisibility(View.VISIBLE);
            t7.setText(""+percent7);
        }else{
            p7.setVisibility(View.GONE);
        }
        if(percent8>=50){
            p8.setVisibility(View.VISIBLE);
            t8.setText(""+percent8);
        }else{
            p8.setVisibility(View.GONE);
        }
        if(percent9>=50){
            p9.setVisibility(View.VISIBLE);
            t9.setText(""+percent9);
        }else{
            p9.setVisibility(View.GONE);
        }
        if(percent10>=50){
            p10.setVisibility(View.VISIBLE);
            t10.setText(""+percent10);
        }else{
            p10.setVisibility(View.GONE);
        }
        if(percent11>=50){
            p11.setVisibility(View.VISIBLE);
            t11.setText(""+percent11);
        }else{
            p11.setVisibility(View.GONE);
        }
        if(percent12>=50){
            p12.setVisibility(View.VISIBLE);
            t12.setText(""+percent12);
        }else{
            p12.setVisibility(View.GONE);
        }
        if(percent13>=50){
            p13.setVisibility(View.VISIBLE);
            t13.setText(""+percent13);
        }else{
            p13.setVisibility(View.GONE);
        }
        if(percent14>=50){
            p14.setVisibility(View.VISIBLE);
            t14.setText(""+percent14);
        }else{
            p14.setVisibility(View.GONE);
        }
        if(percent15>=50){
            p15.setVisibility(View.VISIBLE);
            t15.setText(""+percent15);
        }else{
            p15.setVisibility(View.GONE);
        }
        if(percent16>=50){
            p16.setVisibility(View.VISIBLE);
            t16.setText(""+percent16);
        }else{
            p16.setVisibility(View.GONE);
        }
        if(percent17>=50){
            p17.setVisibility(View.VISIBLE);
            t17.setText(""+percent17);
        }else{
            p17.setVisibility(View.GONE);
        }
        if(percent18>=50){
            p18.setVisibility(View.VISIBLE);
            t18.setText(""+percent18);
        }else{
            p18.setVisibility(View.GONE);
        }
        if(percent19>=50){
            p19.setVisibility(View.VISIBLE);
            t19.setText(""+percent19);
        }else{
            p19.setVisibility(View.GONE);
        }
        if(percent20>=50){
            p20.setVisibility(View.VISIBLE);
            t20.setText(""+percent20);
        }else{
            p20.setVisibility(View.GONE);
        }
        if(percent21>=50){
            p21.setVisibility(View.VISIBLE);
            t21.setText(""+percent21);
        }else{
            p21.setVisibility(View.GONE);
        }
        if(percent22>=50){
            p22.setVisibility(View.VISIBLE);
            t22.setText(""+percent22);
        }else{
            p22.setVisibility(View.GONE);
        }
        if(percent23>=50){
            p23.setVisibility(View.VISIBLE);
            t23.setText(""+percent23);
        }else{
            p23.setVisibility(View.GONE);
        }
        if(percent24>=50){
            p24.setVisibility(View.VISIBLE);
            t24.setText(""+percent24);
        }else{
            p24.setVisibility(View.GONE);
        }
        if(percent25>=50){
            p25.setVisibility(View.VISIBLE);
            t25.setText(""+percent25);
        }else{
            p25.setVisibility(View.GONE);
        }
        if(percent26>=50){
            p26.setVisibility(View.VISIBLE);
            t26.setText(""+percent26);
        }else{
            p26.setVisibility(View.GONE);
        }
        if(percent27>=50){
            p27.setVisibility(View.VISIBLE);
            t27.setText(""+percent27);
        }else{
            p27.setVisibility(View.GONE);
        }
        if(percent28>=50){
            p28.setVisibility(View.VISIBLE);
            t28.setText(""+percent28);
        }else{
            p28.setVisibility(View.GONE);
        }
        if(percent29>=50){
            p29.setVisibility(View.VISIBLE);
            t29.setText(""+percent29);
        }else{
            p29.setVisibility(View.GONE);
        }
        if(percent30>=50){
            p30.setVisibility(View.VISIBLE);
            t30.setText(""+percent30);
        }else{
            p30.setVisibility(View.GONE);
        }
        if(percent31>=50){
            p31.setVisibility(View.VISIBLE);
            t31.setText(""+percent31);
        }else{
            p31.setVisibility(View.GONE);
        }
        if(percent32>=50){
            p32.setVisibility(View.VISIBLE);
            t32.setText(""+percent32);
        }else{
            p32.setVisibility(View.GONE);
        }
        if(percent33>=50){
            p33.setVisibility(View.VISIBLE);
            t33.setText(""+percent33);
        }else{
            p33.setVisibility(View.GONE);
        }
        if(percent34>=50){
            p34.setVisibility(View.VISIBLE);
            t34.setText(""+percent34);
        }else{
            p34.setVisibility(View.GONE);
        }





        //todo work on
        TextView tt0 = view.findViewById(R.id.determinatspercent1);
        TextView tt1 = view.findViewById(R.id.matricespercent1);
        TextView tt2 = view.findViewById(R.id.logrithmicpercent1);
        TextView tt3 = view.findViewById(R.id.seriespercent1);
        TextView tt4 = view.findViewById(R.id.equationpercent1);
        TextView tt5 = view.findViewById(R.id.complexpercent1);
        TextView tt6 = view.findViewById(R.id.pncpercent1);
        TextView tt7 = view.findViewById(R.id.binomialpercent1);
        TextView tt8 = view.findViewById(R.id.probabilitypercent1);
        TextView tt9 = view.findViewById(R.id.functionpercent1);
        TextView tt10 = view.findViewById(R.id.limitpercent1);
        TextView tt11 = view.findViewById(R.id.continutypercent1);
        TextView tt12 = view.findViewById(R.id.differentialbilitypercent1);
        TextView tt13 = view.findViewById(R.id.differentialtionpercent1);
        TextView tt14 = view.findViewById(R.id.monotonicitypercent1);
        TextView tt15 = view.findViewById(R.id.max_minpercent1);
        TextView tt16 = view.findViewById(R.id.tang_normalpercent1);
        TextView tt17 = view.findViewById(R.id.ratepercent1);
        TextView tt18 = view.findViewById(R.id.rolleslangpercent1);
        TextView tt19 = view.findViewById(R.id.indefinitepercent1);
        TextView tt20 = view.findViewById(R.id.definitepercent1);
        TextView tt21 = view.findViewById(R.id.areapercent1);
        TextView tt22 = view.findViewById(R.id.differeneqpercent1);
        TextView tt23 = view.findViewById(R.id.ratio_identitypercent1);
        TextView tt24 = view.findViewById(R.id.trignoeqpercent1);
        TextView tt25 = view.findViewById(R.id.inverse_trigopercent1);
        TextView tt26 = view.findViewById(R.id.height_distpercent1);
        TextView tt27 = view.findViewById(R.id.straight_linepercent1);
        TextView tt28 = view.findViewById(R.id.pair_linepercent1);
        TextView tt29 = view.findViewById(R.id.circlepercent1);
        TextView tt30 = view.findViewById(R.id.parabolapercent1);
        TextView tt31 = view.findViewById(R.id.ellipsepercent1);
        TextView tt32 = view.findViewById(R.id.hyperbolapercent1);
        TextView tt33 = view.findViewById(R.id.three_dpercent1);
        TextView tt34 = view.findViewById(R.id.vectorpercent1);

        LinearLayout pp0 = view.findViewById(R.id.p01);
        LinearLayout pp1 = view.findViewById(R.id.p110);
        LinearLayout pp2 = view.findViewById(R.id.p210);
        LinearLayout pp3 = view.findViewById(R.id.p310);
        LinearLayout pp4 = view.findViewById(R.id.p41);
        LinearLayout pp5 = view.findViewById(R.id.p51);
        LinearLayout pp6 = view.findViewById(R.id.p61);
        LinearLayout pp7 = view.findViewById(R.id.p71);
        LinearLayout pp8 = view.findViewById(R.id.p81);
        LinearLayout pp9 = view.findViewById(R.id.p91);
        LinearLayout pp10 = view.findViewById(R.id.p101);
        LinearLayout pp11 = view.findViewById(R.id.p111);
        LinearLayout pp12 = view.findViewById(R.id.p121);
        LinearLayout pp13 = view.findViewById(R.id.p131);
        LinearLayout pp14 = view.findViewById(R.id.p141);
        LinearLayout pp15 = view.findViewById(R.id.p151);
        LinearLayout pp16 = view.findViewById(R.id.p161);
        LinearLayout pp17 = view.findViewById(R.id.p171);
        LinearLayout pp18 = view.findViewById(R.id.p181);
        LinearLayout pp19 = view.findViewById(R.id.p191);
        LinearLayout pp20 = view.findViewById(R.id.p201);
        LinearLayout pp21 = view.findViewById(R.id.p211);
        LinearLayout pp22 = view.findViewById(R.id.p221);
        LinearLayout pp23 = view.findViewById(R.id.p231);
        LinearLayout pp24 = view.findViewById(R.id.p241);
        LinearLayout pp25 = view.findViewById(R.id.p251);
        LinearLayout pp26 = view.findViewById(R.id.p261);
        LinearLayout pp27 = view.findViewById(R.id.p271);
        LinearLayout pp28 = view.findViewById(R.id.p281);
        LinearLayout pp29 = view.findViewById(R.id.p291);
        LinearLayout pp30 = view.findViewById(R.id.p301);
        LinearLayout pp31 = view.findViewById(R.id.p311);
        LinearLayout pp32 = view.findViewById(R.id.p321);
        LinearLayout pp33 = view.findViewById(R.id.p331);
        LinearLayout pp34 = view.findViewById(R.id.p341);

        if(percent0<=50 && percent0>=0){
            pp0.setVisibility(View.VISIBLE);
            tt0.setText(""+percent0);
        }else{
            pp0.setVisibility(View.GONE);
        }
        if(percent1<=50 && percent1>=0){
            pp1.setVisibility(View.VISIBLE);
            tt1.setText(""+percent1);
        }else{
            pp1.setVisibility(View.GONE);
        }
        if(percent2<=50 && percent2>=0){
            pp2.setVisibility(View.VISIBLE);
            tt2.setText(""+percent2);
        }else{
            pp2.setVisibility(View.GONE);
        }
        if(percent3<=50 && percent3>=0){
            pp3.setVisibility(View.VISIBLE);
            tt3.setText(""+percent3);
        }else{
            pp3.setVisibility(View.GONE);
        }
        if(percent4<=50 && percent4>=0){
            pp4.setVisibility(View.VISIBLE);
            tt4.setText(""+percent4);
        }else{
            pp4.setVisibility(View.GONE);
        }
        if(percent5<=50 && percent5>=0){
            pp5.setVisibility(View.VISIBLE);
            tt5.setText(""+percent5);
        }else{
            pp5.setVisibility(View.GONE);
        }
        if(percent6<=50 && percent6>=0){
            pp6.setVisibility(View.VISIBLE);
            tt6.setText(""+percent6);
        }else{
            pp6.setVisibility(View.GONE);
        }
        if(percent7<=50 && percent7>=0){
            pp7.setVisibility(View.VISIBLE);
            tt7.setText(""+percent7);
        }else{
            pp7.setVisibility(View.GONE);
        }
        if(percent8<=50 && percent8>=0){
            pp8.setVisibility(View.VISIBLE);
            tt8.setText(""+percent8);
        }else{
            pp8.setVisibility(View.GONE);
        }
        if(percent9<=50 && percent9>=0){
            pp9.setVisibility(View.VISIBLE);
            tt9.setText(""+percent9);
        }else{
            pp9.setVisibility(View.GONE);
        }
        if(percent10<=50 && percent10>=0){
            pp10.setVisibility(View.VISIBLE);
            tt10.setText(""+percent10);
        }else{
            pp10.setVisibility(View.GONE);
        }
        if(percent11<=50 && percent11>=0){
            pp11.setVisibility(View.VISIBLE);
            tt11.setText(""+percent11);
        }else{
            pp11.setVisibility(View.GONE);
        }
        if(percent12<=50 && percent12>=0){
            pp12.setVisibility(View.VISIBLE);
            tt12.setText(""+percent12);
        }else{
            pp12.setVisibility(View.GONE);
        }
        if(percent13<=50 && percent13>=0){
            pp13.setVisibility(View.VISIBLE);
            tt13.setText(""+percent13);
        }else{
            pp13.setVisibility(View.GONE);
        }
        if(percent14<=50 && percent14>=0){
            pp14.setVisibility(View.VISIBLE);
            tt14.setText(""+percent14);
        }else{
            pp14.setVisibility(View.GONE);
        }
        if(percent15<=50 && percent15>=0){
            pp15.setVisibility(View.VISIBLE);
            tt15.setText(""+percent15);
        }else{
            pp15.setVisibility(View.GONE);
        }
        if(percent16<=50 && percent16>=0){
            pp16.setVisibility(View.VISIBLE);
            tt16.setText(""+percent16);
        }else{
            pp16.setVisibility(View.GONE);
        }
        if(percent17<=50 && percent17>=0){
            pp17.setVisibility(View.VISIBLE);
            tt17.setText(""+percent17);
        }else{
            pp17.setVisibility(View.GONE);
        }
        if(percent18<=50 && percent18>=0){
            pp18.setVisibility(View.VISIBLE);
            tt18.setText(""+percent18);
        }else{
            pp18.setVisibility(View.GONE);
        }
        if(percent19<=50 && percent19>=0){
            pp19.setVisibility(View.VISIBLE);
            tt19.setText(""+percent19);
        }else{
            pp19.setVisibility(View.GONE);
        }
        if(percent20<=50 && percent20>=0){
            pp20.setVisibility(View.VISIBLE);
            tt20.setText(""+percent20);
        }else{
            pp20.setVisibility(View.GONE);
        }
        if(percent21<=50 && percent21>=0){
            pp21.setVisibility(View.VISIBLE);
            tt21.setText(""+percent21);
        }else{
            pp21.setVisibility(View.GONE);
        }
        if(percent22<=50 && percent22>=0){
            pp22.setVisibility(View.VISIBLE);
            tt22.setText(""+percent22);
        }else{
            pp22.setVisibility(View.GONE);
        }
        if(percent23<=50 && percent23>=0){
            pp23.setVisibility(View.VISIBLE);
            tt23.setText(""+percent23);
        }else{
            pp23.setVisibility(View.GONE);
        }
        if(percent24<=50 && percent24>=0){
            pp24.setVisibility(View.VISIBLE);
            tt24.setText(""+percent24);
        }else{
            pp24.setVisibility(View.GONE);
        }
        if(percent25<=50 && percent25>=0){
            pp25.setVisibility(View.VISIBLE);
            tt25.setText(""+percent25);
        }else{
            pp25.setVisibility(View.GONE);
        }
        if(percent26<=50 && percent26>=0){
            pp26.setVisibility(View.VISIBLE);
            tt26.setText(""+percent26);
        }else{
            pp26.setVisibility(View.GONE);
        }
        if(percent27<=50 && percent27>=0){
            pp27.setVisibility(View.VISIBLE);
            tt27.setText(""+percent27);
        }else{
            pp27.setVisibility(View.GONE);
        }
        if(percent28<=50 && percent28>=0){
            pp28.setVisibility(View.VISIBLE);
            tt28.setText(""+percent28);
        }else{
            pp28.setVisibility(View.GONE);
        }
        if(percent29<=50 && percent29>=0){
            pp29.setVisibility(View.VISIBLE);
            tt29.setText(""+percent29);
        }else{
            pp29.setVisibility(View.GONE);
        }
        if(percent30<=50 && percent30>=0){
            pp30.setVisibility(View.VISIBLE);
            tt30.setText(""+percent30);
        }else{
            pp30.setVisibility(View.GONE);
        }
        if(percent31<=50 && percent31>=0){
            pp31.setVisibility(View.VISIBLE);
            tt31.setText(""+percent31);
        }else{
            pp31.setVisibility(View.GONE);
        }
        if(percent32<=50 && percent32>=0){
            pp32.setVisibility(View.VISIBLE);
            tt32.setText(""+percent32);
        }else{
            pp32.setVisibility(View.GONE);
        }
        if(percent33<=50 && percent33>=0){
            pp33.setVisibility(View.VISIBLE);
            tt33.setText(""+percent33);
        }else{
            pp33.setVisibility(View.GONE);
        }
        if(percent34<=50 && percent34>=0){
            pp34.setVisibility(View.VISIBLE);
            tt34.setText(""+percent34);
        }else{
            pp34.setVisibility(View.GONE);
        }


        p0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "determinants");
                context.startActivity(intent);
            }
        });
        p1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "matrices");
                context.startActivity(intent);
            }
        });

        p2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "logarithmic");
                context.startActivity(intent);
            }
        });
        p3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "series");
                context.startActivity(intent);
            }
        });

        p4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "equation");
                context.startActivity(intent);
            }
        });
        p5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "complex");
                context.startActivity(intent);
            }
        });
        p6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "pnc");
                context.startActivity(intent);
            }
        });
        p7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "binomial");
                context.startActivity(intent);
            }
        });

        p8.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "probability");
                context.startActivity(intent);
            }
        });
        p9.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "functions");
                context.startActivity(intent);
            }
        });
        p10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "limit");
                context.startActivity(intent);
            }
        });
        p11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "continuity");
                context.startActivity(intent);
            }
        });
        p12.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "differentiability");
                context.startActivity(intent);
            }
        });
        p13.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "differentiation");
                context.startActivity(intent);
            }
        });
        p14.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "monotonicity");
                context.startActivity(intent);
            }
        });
        p15.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "max_min");
                context.startActivity(intent);
            }
        });
        p16.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "tan_normal");
                context.startActivity(intent);
            }
        });

        p17.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "ic_action_important");
                context.startActivity(intent);
            }
        });
        p18.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "lagrange");
                context.startActivity(intent);
            }
        });

        p19.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "indefinite_inti");
                context.startActivity(intent);
            }
        });
        p20.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "definite_inti");
                context.startActivity(intent);
            }
        });
        p21.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "area");
                context.startActivity(intent);
            }
        });
        p22.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "differen_eqn");
                context.startActivity(intent);
            }
        });
        p23.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "ratio_identity");
                context.startActivity(intent);
            }
        });
        p24.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "trigo_eq");
                context.startActivity(intent);
            }
        });
        p25.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "inverse_trigo");
                context.startActivity(intent);
            }
        });
        p26.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "heigh_dist");
                context.startActivity(intent);
            }
        });
        p27.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "straight_line");
                context.startActivity(intent);
            }
        });
        p28.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "pair_straight_line");
                context.startActivity(intent);
            }
        });
        p29.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "circle");
                context.startActivity(intent);
            }
        });
        p30.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "parabola");
                context.startActivity(intent);
            }
        });
        p31.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "ellipse");
                context.startActivity(intent);
            }
        });
        p32.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "hyperbola");
                context.startActivity(intent);
            }
        });
        p33.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","three_d");
                intent.putExtra("chapter", "three_d");
                context.startActivity(intent);
            }
        });
        p34.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","vector");
                intent.putExtra("chapter", "vector");
                context.startActivity(intent);
            }
        });

        pp0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "determinants");
                context.startActivity(intent);
            }
        });
        pp1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "matrices");
                context.startActivity(intent);
            }
        });

        pp2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "logarithmic");
                context.startActivity(intent);
            }
        });
        pp3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "series");
                context.startActivity(intent);
            }
        });

        pp4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "equation");
                context.startActivity(intent);
            }
        });
        pp5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "complex");
                context.startActivity(intent);
            }
        });
        pp6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "pnc");
                context.startActivity(intent);
            }
        });
        pp7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "binomial");
                context.startActivity(intent);
            }
        });

        pp8.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","algebra");
                intent.putExtra("chapter", "probability");
                context.startActivity(intent);
            }
        });
        pp9.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "functions");
                context.startActivity(intent);
            }
        });
        pp10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "limit");
                context.startActivity(intent);
            }
        });
        pp11.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "continuity");
                context.startActivity(intent);
            }
        });
        pp12.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "differentiability");
                context.startActivity(intent);
            }
        });
        pp13.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "differentiation");
                context.startActivity(intent);
            }
        });
        pp14.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "monotonicity");
                context.startActivity(intent);
            }
        });
        pp15.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "max_min");
                context.startActivity(intent);
            }
        });
        pp16.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "tan_normal");
                context.startActivity(intent);
            }
        });

        pp17.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "ic_action_important");
                context.startActivity(intent);
            }
        });
        pp18.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","differential");
                intent.putExtra("chapter", "lagrange");
                context.startActivity(intent);
            }
        });

        pp19.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "indefinite_inti");
                context.startActivity(intent);
            }
        });
        pp20.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "definite_inti");
                context.startActivity(intent);
            }
        });
        pp21.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "area");
                context.startActivity(intent);
            }
        });
        pp22.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","integral");
                intent.putExtra("chapter", "differen_eqn");
                context.startActivity(intent);
            }
        });
        pp23.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "ratio_identity");
                context.startActivity(intent);
            }
        });
        pp24.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "trigo_eq");
                context.startActivity(intent);
            }
        });
        pp25.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "inverse_trigo");
                context.startActivity(intent);
            }
        });
        pp26.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","trigonometry");
                intent.putExtra("chapter", "heigh_dist");
                context.startActivity(intent);
            }
        });
        pp27.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "straight_line");
                context.startActivity(intent);
            }
        });
        pp28.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "pair_straight_line");
                context.startActivity(intent);
            }
        });
        pp29.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "circle");
                context.startActivity(intent);
            }
        });
        pp30.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "parabola");
                context.startActivity(intent);
            }
        });
        pp31.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "ellipse");
                context.startActivity(intent);
            }
        });
        pp32.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","two_d");
                intent.putExtra("chapter", "hyperbola");
                context.startActivity(intent);
            }
        });
        pp33.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","three_d");
                intent.putExtra("chapter", "three_d");
                context.startActivity(intent);
            }
        });
        pp34.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Concepts.class);
                intent.putExtra("book","vector");
                intent.putExtra("chapter", "vector");
                context.startActivity(intent);
            }
        });



    }

    public void defaultSettings(View view){
        TextView tscore = view.findViewById(R.id.tscore);
        TextView correct = view.findViewById(R.id.correct);
        TextView wrong = view.findViewById(R.id.wrong);
        TextView goodat = view.findViewById(R.id.goodat);
        TextView chapters = view.findViewById(R.id.chapters);
        TextView percent = view.findViewById(R.id.percent);
        TextView determinants = view.findViewById(R.id.determinats);
        TextView matrices = view.findViewById(R.id.matrices);
        TextView logarithmic = view.findViewById(R.id.logrithmic);
        TextView series = view.findViewById(R.id.series);
        TextView equation = view.findViewById(R.id.equation);
        TextView complex = view.findViewById(R.id.complex);
        TextView pnc = view.findViewById(R.id.pnc);
        TextView binomial = view.findViewById(R.id.binomial);
        TextView probbility = view.findViewById(R.id.probbility);
        TextView function = view.findViewById(R.id.function);
        TextView limit = view.findViewById(R.id.limit);
        TextView continuity = view.findViewById(R.id.continuity);
        TextView differentiability = view.findViewById(R.id.differentiability);
        TextView differentiation = view.findViewById(R.id.differentiation);
        TextView monotonicity = view.findViewById(R.id.monotonicity);
        TextView max_min = view.findViewById(R.id.max_min);
        TextView tangentnormal = view.findViewById(R.id.tangentnormal);
        TextView rate = view.findViewById(R.id.rate);
        TextView rolleslang = view.findViewById(R.id.rolleslang);
        TextView indefinite = view.findViewById(R.id.indefinte);
        TextView definite = view.findViewById(R.id.definite);
        TextView area = view.findViewById(R.id.area);
        TextView differeneq = view.findViewById(R.id.differeneq);
        TextView ratio_identity = view.findViewById(R.id.ratio_identity);
        TextView trignoeq = view.findViewById(R.id.trignoeq);
        TextView inverse_trigo = view.findViewById(R.id.inverse_trigo);
        TextView height_dist = view.findViewById(R.id.height_dist);
        TextView straight_line = view.findViewById(R.id.straight_line);
        TextView pair_line = view.findViewById(R.id.pair_line);
        TextView circle = view.findViewById(R.id.circle);
        TextView parabola = view.findViewById(R.id.parabola);
        TextView ellipse = view.findViewById(R.id.ellipse);
        TextView hyperbola = view.findViewById(R.id.hyperbola);
        TextView three_d = view.findViewById(R.id.three_d);
        TextView vector = view.findViewById(R.id.vector);

        try{
            tscore.setText(R.string.tscore);
            correct.setText(R.string.correct);
            wrong.setText(R.string.wrong);
            goodat.setText(R.string.goodat);
            chapters.setText(R.string.chapter);
            percent.setText(R.string.percentsign);

        }catch (Exception e){
        }



        try{
            determinants.setText(R.string.deternant);
            matrices.setText(R.string.matices);
            logarithmic.setText(R.string.logrithmic);
            series.setText(R.string.series);
            equation.setText(R.string.equation);
            complex.setText(R.string.complex);
            pnc.setText(R.string.pnc);
            binomial.setText(R.string.binomial);
            probbility.setText(R.string.probability);
            function.setText(R.string.functions);
            limit.setText(R.string.limit);
            continuity.setText(R.string.continuty);

        }catch (Exception e){
        }

        try{
            differentiability.setText(R.string.differentiability);
            differentiation.setText(R.string.differentiation);
            monotonicity.setText(R.string.monottonicity);
            max_min.setText(R.string.maxmin);
            tangentnormal.setText(R.string.tangenNormal);
            rate.setText(R.string.rate);
            rolleslang.setText(R.string.rolleLangranges);
            indefinite.setText(R.string.indefinite);
            definite.setText(R.string.definite);
            area.setText(R.string.area);
            differeneq.setText(R.string.differentialeq);
            ratio_identity.setText(R.string.rationIdent);
            trignoeq.setText(R.string.trignoeq);

        }catch (Exception e){
        }

        try{
            inverse_trigo.setText(R.string.inverseTrigo);
            height_dist.setText(R.string.heightDist);
            straight_line.setText(R.string.stratline);
            pair_line.setText(R.string.pairofstratline);
            circle.setText(R.string.circle);
            parabola.setText(R.string.parabola);
            ellipse.setText(R.string.ellipse);
            hyperbola.setText(R.string.hyperbola);
            three_d.setText(R.string.threed);
            vector.setText(R.string.vectr);

        }catch (Exception e){
        }
//todo setvalue of all chapters goodat



//todo setvalues of all chapters workon (add 1 in all)
        TextView workon = view.findViewById(R.id.workon);

        try{
            workon.setText(R.string.workon);
        }catch (Exception e){
        }

        TextView chapters1 = view.findViewById(R.id.chapters1);
        TextView percent1 = view.findViewById(R.id.percent1);
        TextView determinants1 = view.findViewById(R.id.determinats1);
        TextView matrices1 = view.findViewById(R.id.matrices1);
        TextView logarithmic1 = view.findViewById(R.id.logrithmic1);
        TextView series1 = view.findViewById(R.id.series1);
        TextView equation1 = view.findViewById(R.id.equation1);
        TextView complex1 = view.findViewById(R.id.complex1);
        TextView pnc1 = view.findViewById(R.id.pnc1);
        TextView binomial1 = view.findViewById(R.id.binomial1);
        TextView probbility1 = view.findViewById(R.id.probbility1);
        TextView function1 = view.findViewById(R.id.function1);
        TextView limit1 = view.findViewById(R.id.limit1);
        TextView continuity1 = view.findViewById(R.id.continuity1);
        TextView differentiability1 = view.findViewById(R.id.differentiability1);
        TextView differentiation1 = view.findViewById(R.id.differentiation1);
        TextView monotonicity1 = view.findViewById(R.id.monotonicity1);
        TextView max_min1 = view.findViewById(R.id.max_min1);
        TextView tangentnormal1 = view.findViewById(R.id.tangentnormal1);
        TextView rate1 = view.findViewById(R.id.rate1);
        TextView rolleslang1 = view.findViewById(R.id.rolleslang1);
        TextView indefinite1 = view.findViewById(R.id.indefinte1);
        TextView definite1 = view.findViewById(R.id.definite1);
        TextView area1 = view.findViewById(R.id.area1);
        TextView differeneq1 = view.findViewById(R.id.differeneq1);
        TextView ratio_identity1 = view.findViewById(R.id.ratio_identity1);
        TextView trignoeq1 = view.findViewById(R.id.trignoeq1);
        TextView inverse_trigo1 = view.findViewById(R.id.inverse_trigo1);
        TextView height_dist1 = view.findViewById(R.id.height_dist1);
        TextView straight_line1 = view.findViewById(R.id.straight_line1);
        TextView pair_line1 = view.findViewById(R.id.pair_line1);
        TextView circle1 = view.findViewById(R.id.circle1);
        TextView parabola1 = view.findViewById(R.id.parabola1);
        TextView ellipse1 = view.findViewById(R.id.ellipse1);
        TextView hyperbola1 = view.findViewById(R.id.hyperbola1);
        TextView three_d1 = view.findViewById(R.id.three_d1);
        TextView vector1 = view.findViewById(R.id.vector1);


        try{
            chapters1.setText(R.string.chapter);
            percent1.setText(R.string.percentsign);
        }catch (Exception e){
        }


        try{
            determinants1.setText(R.string.deternant);
            matrices1.setText(R.string.matices);
            logarithmic1.setText(R.string.logrithmic);
            series1.setText(R.string.series);
            equation1.setText(R.string.equation);
            complex1.setText(R.string.complex);
            pnc1.setText(R.string.pnc);
            binomial1.setText(R.string.binomial);
            probbility1.setText(R.string.probability);
            function1.setText(R.string.functions);
            limit1.setText(R.string.limit);
            continuity1.setText(R.string.continuty);

        }catch (Exception e){
        }

        try{
            differentiability1.setText(R.string.differentiability);
            differentiation1.setText(R.string.differentiation);
            monotonicity1.setText(R.string.monottonicity);
            max_min1.setText(R.string.maxmin);
            tangentnormal1.setText(R.string.tangenNormal);
            rate1.setText(R.string.rate);
            rolleslang1.setText(R.string.rolleLangranges);
            indefinite1.setText(R.string.indefinite);
            definite1.setText(R.string.definite);
            area1.setText(R.string.area);

        }catch (Exception e){
        }

        try{
            differeneq1.setText(R.string.differentialeq);
            ratio_identity1.setText(R.string.rationIdent);
            trignoeq1.setText(R.string.trignoeq);
            inverse_trigo1.setText(R.string.inverseTrigo);
            height_dist1.setText(R.string.heightDist);
            straight_line1.setText(R.string.stratline);
            pair_line1.setText(R.string.pairofstratline);
            circle1.setText(R.string.circle);

        }catch (Exception e){
        }

        try{
            parabola1.setText(R.string.parabola);
            ellipse1.setText(R.string.ellipse);
            hyperbola1.setText(R.string.hyperbola);
            three_d1.setText(R.string.threed);
            vector1.setText(R.string.vectr);

        }catch (Exception e){
        }

    }

}
