package com.example.course_booking_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseAddFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CourseAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseAddFragment.
     */
    // TODO: Rename and change types and number of parameters

    protected EditText editCourseName, editCourseInstructor, editCourseCode;
    protected Button addCourse;

    public static CourseAddFragment newInstance(String param1, String param2) {
        CourseAddFragment fragment = new CourseAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_add, container, false);

        editCourseName = (EditText) view.findViewById(R.id.editCourseName);
        editCourseCode = (EditText) view.findViewById(R.id.editCourseCode);
        editCourseInstructor = (EditText) view.findViewById(R.id.editInstructor);
        addCourse = (Button) view.findViewById(R.id.addCourse);

        if(((CoursesActivity)getActivity()).isAdd == false){
            editCourseName.setText(((CoursesActivity)getActivity()).modifiedCourse.getName());
            editCourseCode.setText(((CoursesActivity)getActivity()).modifiedCourse.getCode());
            editCourseInstructor.setText(((CoursesActivity)getActivity()).modifiedCourse.getInstructor());
        }

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the user
                String tempCode = editCourseCode.getText().toString();
                String tempName = editCourseName.getText().toString();
                String tempInstructor = editCourseInstructor.getText().toString();

                String toastMessage = "";

                boolean acceptable = false;

                while(acceptable == false){
                    if(tempCode.length() < 7){
                        toastMessage = "Course code must be at least 7 characters in length!";
                    }
                    else if(tempName.length() < 5){
                        toastMessage = "The course name field must not be empty!";
                    }
                    else if(tempInstructor.length() < 5){
                        toastMessage = "The course instructor field must not be empty!";
                    }
                    else{
                        acceptable = true;
                        if(((CoursesActivity)getActivity()).isAdd == true){
                            toastMessage = "New course has been added!";
                        }
                        else{
                            toastMessage = "Existing course has been modified!";
                        }
                    }
                    Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                }

                if(((CoursesActivity)getActivity()).isAdd == true){
                    ((CoursesActivity)getActivity()).dbHandler.addCourse(tempCode, tempName, tempInstructor);
                }
                else {
                    ContentValues cv = new ContentValues();
                    ((CoursesActivity)getActivity()).dbHandler.modifyCourse(
                            ((CoursesActivity)getActivity()).editEntry,
                            tempCode,
                            tempName,
                            tempInstructor);
                }

                ((CoursesActivity)getActivity()).courseRVAdapter.notifyDataSetChanged();

                //this fragment will now remove itself
                getActivity().getSupportFragmentManager().beginTransaction().remove(CourseAddFragment.this).commit();
            }
        });

        return view;
    }

}