package com.naresh.kingupadhyay.mathsking.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.naresh.kingupadhyay.mathsking.activities.Concepts;
import com.naresh.kingupadhyay.mathsking.activities.CourseDetails;
import com.naresh.kingupadhyay.mathsking.R;

public class Course extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.content_main, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button algebra;
        public Button determinants;
        public Button matrices;
        public Button logarithmic;
        public Button series;
        public Button equation;
        public Button complex;
        public Button pnc;
        public Button binomial;
        public Button probability;
        public Button differential;
        public Button functions;
        public Button limit;
        public Button continuity;
        public Button differentiability;
        public Button differentiation;
        public Button monotonicity;
        public Button max_min;
        public Button tan_normal;
        public Button rate;
        public Button lagrange;
        public Button integral;
        public Button indefinite_inti;
        public Button definite_inti;
        public Button area;
        public Button differen_eqn;
        public Button trigonometry;
        public Button ratio_identity;
        public Button trigo_eq;
        public Button inverse_trigo;
        public Button heigh_dist;
        public Button two_d;
        public Button straight_line;
        public Button pair_straight_line;
        public Button circle;
        public Button parabola;
        public Button ellipse;
        public Button hyperbola;
        public Button three_d;
        public Button vector;
        public RelativeLayout rl_algebra;
        public RelativeLayout rl_differential;
        public RelativeLayout rl_integral;
        public RelativeLayout rl_trigonometry;
        public RelativeLayout rl_twoD;
        public boolean help= true;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_course, parent, false));

            rl_algebra=itemView.findViewById(R.id.child_layout);
            rl_algebra.setVisibility(View.GONE);
            algebra = (Button)itemView.findViewById(R.id.algebra);
            algebra.setText("      1.      Algebra(and others)");
            algebra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_expand, 0);
            algebra.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(rl_algebra.getVisibility()==View.GONE){
                        rl_algebra.setVisibility(View.VISIBLE);
                        algebra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_collapse, 0);
                        algebra.setCompoundDrawablePadding(10);
                        //algebra.setTextColor(Color.BLACK);
                    }else {
                        rl_algebra.setVisibility(View.GONE);
                        algebra.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_expand, 0);
                    }

                }
            });
            determinants = (Button)itemView.findViewById(R.id.determinants);
            determinants.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "determinants");
                    intent.putExtra("title", "Determinants");
                    context.startActivity(intent);
                }
            });
            matrices= (Button)itemView.findViewById(R.id.matrices);
            matrices.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "matrices");
                    intent.putExtra("title", "Matrices");
                    context.startActivity(intent);
                }
            });

            logarithmic = (Button)itemView.findViewById(R.id.logarithmic);
            logarithmic.setText("All others");
            logarithmic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "logarithmic");
                    intent.putExtra("title", "Logarithmic");
                    context.startActivity(intent);
                }
            });
            series = (Button)itemView.findViewById(R.id.series);
            series.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "series");
                    intent.putExtra("title", "Series");
                    context.startActivity(intent);
                }
            });

            equation = (Button)itemView.findViewById(R.id.equation);
            equation.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "equation");
                    intent.putExtra("title", "Equation");
                    context.startActivity(intent);
                }
            });
            complex= (Button)itemView.findViewById(R.id.complex);
            complex.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "complex");
                    intent.putExtra("title", "Complex");
                    context.startActivity(intent);
                }
            });
            pnc = (Button)itemView.findViewById(R.id.pnc);
            pnc.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "pnc");
                    intent.putExtra("title", "Permutation and combination");
                    context.startActivity(intent);
                }
            });
            binomial = (Button)itemView.findViewById(R.id.binomial);
            binomial.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "binomial");
                    intent.putExtra("title", "Binomial");
                    context.startActivity(intent);
                }
            });

            probability = (Button)itemView.findViewById(R.id.probability);
            probability.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","algebra");
                    intent.putExtra("chapter", "probability");
                    intent.putExtra("title", "Probability");
                    context.startActivity(intent);
                }
            });

            rl_differential=itemView.findViewById(R.id.child_layout2);
            rl_differential.setVisibility(View.GONE);
            differential = (Button)itemView.findViewById(R.id.differential);
            differential.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_expand, 0);
            differential.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(rl_differential.getVisibility()==View.GONE){
                        rl_differential.setVisibility(View.VISIBLE);
                        differential.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_collapse, 0);
                        differential.setCompoundDrawablePadding(10);
                        //algebra.setTextColor(Color.BLACK);
                    }else {
                        rl_differential.setVisibility(View.GONE);
                        differential.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_expand, 0);
                    }

                }
            });
            functions = (Button)itemView.findViewById(R.id.functions);
            functions.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "functions");
                    intent.putExtra("title", "Functions");
                    context.startActivity(intent);
                }
            });
            limit= (Button)itemView.findViewById(R.id.limit);
            limit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "limit");
                    intent.putExtra("title", "Limit");
                    context.startActivity(intent);
                }
            });
            continuity = (Button)itemView.findViewById(R.id.continuity);
            continuity.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "continuity");
                    intent.putExtra("title", "Continuity");
                    context.startActivity(intent);
                }
            });
            differentiability = (Button)itemView.findViewById(R.id.differentiability);
            differentiability.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "differentiability");
                    intent.putExtra("title", "Differentiability");
                    context.startActivity(intent);
                }
            });
            differentiation = (Button)itemView.findViewById(R.id.differentiation);
            differentiation.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "differentiation");
                    intent.putExtra("title", "Differentiation");
                    context.startActivity(intent);
                }
            });
            monotonicity= (Button)itemView.findViewById(R.id.monotonicity);
            monotonicity.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "monotonicity");
                    intent.putExtra("title", "Monotonicity");
                    context.startActivity(intent);
                }
            });
            max_min = (Button)itemView.findViewById(R.id.max_min);
            max_min.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "max_min");
                    intent.putExtra("title", "Maxima and Minima");
                    context.startActivity(intent);
                }
            });
            tan_normal = (Button)itemView.findViewById(R.id.tang_normal);
            tan_normal.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "tan_normal");
                    intent.putExtra("title", "Tangent and Normal");
                    context.startActivity(intent);
                }
            });

            rate = (Button)itemView.findViewById(R.id.rate);
            rate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "ic_action_important");
                    intent.putExtra("title", "Rate");
                    context.startActivity(intent);
                }
            });
            lagrange= (Button)itemView.findViewById(R.id.lagrange);
            lagrange.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","differential");
                    intent.putExtra("chapter", "lagrange");
                    intent.putExtra("title", "Lagrange");
                    context.startActivity(intent);
                }
            });

            rl_integral=itemView.findViewById(R.id.child_layout3);
            rl_integral.setVisibility(View.GONE);
            integral = (Button)itemView.findViewById(R.id.integral);
            integral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_expand, 0);
            integral.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(rl_integral.getVisibility()==View.GONE){
                        rl_integral.setVisibility(View.VISIBLE);
                        integral.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_collapse, 0);
                        integral.setCompoundDrawablePadding(10);
                        //algebra.setTextColor(Color.BLACK);
                    }else {
                        rl_integral.setVisibility(View.GONE);
                        integral.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_expand, 0);
                    }

                }
            });
            indefinite_inti = (Button)itemView.findViewById(R.id.indefinite);
            indefinite_inti.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","integral");
                    intent.putExtra("chapter", "indefinite_inti");
                    intent.putExtra("title", "Indefinite Integral");
                    context.startActivity(intent);
                }
            });
            definite_inti = (Button)itemView.findViewById(R.id.Definite);
            definite_inti.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","integral");
                    intent.putExtra("chapter", "definite_inti");
                    intent.putExtra("title", "Definite Integral");
                    context.startActivity(intent);
                }
            });
            area = (Button)itemView.findViewById(R.id.area);
            area.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","integral");
                    intent.putExtra("chapter", "area");
                    intent.putExtra("title", "Area");
                    context.startActivity(intent);
                }
            });
            differen_eqn = (Button)itemView.findViewById(R.id.diff_eqn);
            differen_eqn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","integral");
                    intent.putExtra("chapter", "differen_eqn");
                    intent.putExtra("title", "Differential Equation");
                    context.startActivity(intent);
                }
            });
            rl_trigonometry=itemView.findViewById(R.id.child_layout4);
            rl_trigonometry.setVisibility(View.GONE);
            trigonometry = (Button)itemView.findViewById(R.id.trigonometry);
            trigonometry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_expand, 0);
            trigonometry.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(rl_trigonometry.getVisibility()==View.GONE){
                        rl_trigonometry.setVisibility(View.VISIBLE);
                        trigonometry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_collapse, 0);
                        trigonometry.setCompoundDrawablePadding(10);
                        //algebra.setTextColor(Color.BLACK);
                    }else {
                        rl_trigonometry.setVisibility(View.GONE);
                        trigonometry.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_expand, 0);
                    }

                }
            });
            ratio_identity = (Button)itemView.findViewById(R.id.ratio_identity);
            ratio_identity.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","trigonometry");
                    intent.putExtra("chapter", "ratio_identity");
                    intent.putExtra("title", "Ratio and Identity");
                    context.startActivity(intent);
                }
            });
            trigo_eq = (Button)itemView.findViewById(R.id.trigo_eqn);
            trigo_eq.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","trigonometry");
                    intent.putExtra("chapter", "trigo_eq");
                    intent.putExtra("title", "Trigonometric equation");
                    context.startActivity(intent);
                }
            });
            inverse_trigo = (Button)itemView.findViewById(R.id.inverse_trigo);
            inverse_trigo.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","trigonometry");
                    intent.putExtra("chapter", "inverse_trigo");
                    intent.putExtra("title", "Inverse Trigonometric");
                    context.startActivity(intent);
                }
            });
            heigh_dist = (Button)itemView.findViewById(R.id.height_dist);
            heigh_dist.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","trigonometry");
                    intent.putExtra("chapter", "heigh_dist");
                    intent.putExtra("title", "Height and Distance");
                    context.startActivity(intent);
                }
            });
            rl_twoD=itemView.findViewById(R.id.child_layout5);
            rl_twoD.setVisibility(View.GONE);
            two_d = (Button)itemView.findViewById(R.id.geometry2d);
            two_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_expand, 0);
            two_d.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(rl_twoD.getVisibility()==View.GONE){
                        rl_twoD.setVisibility(View.VISIBLE);
                        two_d.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_collapse, 0);
                        two_d.setCompoundDrawablePadding(10);
                        //algebra.setTextColor(Color.BLACK);
                    }else {
                        rl_twoD.setVisibility(View.GONE);
                        two_d.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_action_expand, 0);
                    }

                }
            });
            straight_line = (Button)itemView.findViewById(R.id.straight_line);
            straight_line.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "straight_line");
                    intent.putExtra("title", "Straight line");
                    context.startActivity(intent);
                }
            });
            pair_straight_line = (Button)itemView.findViewById(R.id.pair_line);
            pair_straight_line.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "pair_straight_line");
                    intent.putExtra("title", "Pair of Straight line");
                    context.startActivity(intent);
                }
            });
            circle = (Button)itemView.findViewById(R.id.circle);
            circle.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "circle");
                    intent.putExtra("title", "Circle");
                    context.startActivity(intent);
                }
            });
            parabola = (Button)itemView.findViewById(R.id.parabola);
            parabola.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "parabola");
                    intent.putExtra("title", "Parabola");
                    context.startActivity(intent);
                }
            });
            ellipse = (Button)itemView.findViewById(R.id.ellipse);
            ellipse.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "ellipse");
                    intent.putExtra("title", "Ellipse");
                    context.startActivity(intent);
                }
            });
            hyperbola = (Button)itemView.findViewById(R.id.hyperbola);
            hyperbola.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","two_d");
                    intent.putExtra("chapter", "hyperbola");
                    intent.putExtra("title", "Hyperbola");
                    context.startActivity(intent);
                }
            });


            three_d = (Button)itemView.findViewById(R.id.three_d);
            three_d.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","three_d");
                    intent.putExtra("chapter", "three_d");
                    intent.putExtra("title", "Three Dimensional Geometry");
                    context.startActivity(intent);
                }
            });
            vector = (Button)itemView.findViewById(R.id.vector);
            vector.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Concepts.class);
                    intent.putExtra("book","vector");
                    intent.putExtra("chapter", "vector");
                    intent.putExtra("title", "Vector");
                    context.startActivity(intent);
                }
            });


        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private static final int LENGTH = 1;

        public ContentAdapter(Context context) {
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }

}

