package com.naresh.kingupadhyay.mathsking.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class downloadFirestoreData {
    private LoadConcept city;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public downloadFirestoreData(){}
    public downloadFirestoreData(String book,String chapter){
        DocumentReference courseRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("course");

        courseRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                city = documentSnapshot.toObject(LoadConcept.class);
            }
        });

    }

    public void customObjects() {
        // [START custom_objects]
        DocumentReference docRef = db.collection("cities").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LoadConcept city = documentSnapshot.toObject(LoadConcept.class);
            }
        });
        // [END custom_objects]
    }


    public void docReference() {
        // [START doc_reference]
        DocumentReference alovelaceDocumentRef = db.collection("users").document("alovelace");
        // [END doc_reference]
    }

    public void collectionReference() {
        // [START collection_reference]
        CollectionReference usersCollectionRef = db.collection("users");
        // [END collection_reference]
    }

    public void docReferenceAlternate() {
        // [START doc_reference_alternate]
        DocumentReference alovelaceDocumentRef = db.document("users/alovelace");
        // [END doc_reference_alternate]
    }

    public void addCustomClass() {
        // [START add_custom_class] 5000000L
//        LoadConcept city = new LoadConcept("Los Angeles", "CA","hello",
//                Arrays.asList((Object)"west_coast"), Arrays.asList((Object)"sorcal"),Arrays.asList((Object)"sorcal"));
//        db.collection("cities").document("LA").set(city);
        // [END add_custom_class]
    }

    public void getDocument() {
        // [START get_document]
        DocumentReference docRef = db.collection("cities").document("SF");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
//                        Log.d(TAG, "No such document");
                    }
                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // [END get_document]
    }

    public void getDocumentWithOptions() {
        // [START get_document_options]
        DocumentReference docRef = db.collection("cities").document("SF");

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;

        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
//                    Log.d(TAG, "Cached document data: " + document.getData());
                } else {
//                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
        // [END get_document_options]
    }

    public void listenToMultiple() {
        // [START listen_multiple]
        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("name") != null) {
                                cities.add(doc.getString("name"));
                            }
                        }
//                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
        // [END listen_multiple]
    }
}


