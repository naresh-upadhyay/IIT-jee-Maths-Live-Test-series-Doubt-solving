package com.naresh.kingupadhyay.mathsking.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class uploadFirestoreData {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public uploadFirestoreData(){}
    public uploadFirestoreData(String book,String chapter){
        DocumentReference courseRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("course");
//        LoadConcept city = new LoadConcept("Los Angeles", "CA","hello",
//                Arrays.asList((Object)"west_coast"), Arrays.asList((Object)"sorcal"),Arrays.asList((Object)"sorcal"));
//        db.collection("cities").document("LA").set(city);
    }

    public void newDocument() {
        // [START new_document]
        Map<String, Object> data = new HashMap<>();

        DocumentReference newCityRef = db.collection("cities").document();

        // Later...
        newCityRef.set(data);
        // [END new_document]
    }

    public void updateDocument() {
        // [START update_document]
        DocumentReference washingtonRef = db.collection("cities").document("DC");

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("capital", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
                    }
                });
        // [END update_document]
    }


    public void updateDocumentNested() {
        // [START update_document_nested]
        // Assume the document contains:
        // {
        //   name: "Frank",
        //   favorites: { food: "Pizza", color: "Blue", subject: "recess" }
        //   age: 12
        // }
        //
        // To update age and favorite color:
        db.collection("users").document("frank")
                .update(
                        "age", 13,
                        "favorites.color", "Red"
                );
        // [END update_document_nested]
    }

    public void dataTypes() {
        // [START data_types]
        Map<String, Object> docData = new HashMap<>();
        docData.put("stringExample", "Hello world!");
        docData.put("booleanExample", true);
        docData.put("numberExample", 3.14159265);
        docData.put("dateExample", new Timestamp(new Date()));
        docData.put("listExample", Arrays.asList(1, 2, 3));
        docData.put("nullExample", null);

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("a", 5);
        nestedData.put("b", true);

        docData.put("objectExample", nestedData);

        db.collection("data").document("one")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
                    }
                });
        // [END data_types]
    }
}
