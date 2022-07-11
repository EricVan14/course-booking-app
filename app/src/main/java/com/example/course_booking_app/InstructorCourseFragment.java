package com.example.course_booking_app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InstructorCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InstructorCourseFragment extends Fragment {

    public InstructorCourseFragment() {
        // Required empty public constructor
    }

    protected TextView textCourseCode, textCourseName, textCourseInstructor;
    protected EditText editDescription, editCapacity, editDays, editHours;
    protected Button saveChanges, close, assign;

    public static InstructorCourseFragment newInstance(String param1, String param2) {
        InstructorCourseFragment fragment = new InstructorCourseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_instructor_course, container, false);

        //create the widgets
        textCourseCode = view.findViewById(R.id.textCourseCode);
        textCourseName = view.findViewById(R.id.textCourseName);
        textCourseInstructor = view.findViewById(R.id.textCourseInstructor);

        editDescription = view.findViewById(R.id.editDescription);
        editCapacity = view.findViewById(R.id.editCapacity);
        editDays = view.findViewById(R.id.editDays);
        editHours = view.findViewById(R.id.editHours);

        saveChanges = view.findViewById(R.id.saveChanges);
        close = view.findViewById(R.id.close);
        assign = view.findViewById(R.id.assign);

        //initialize the text for our widgets
        textCourseCode.setText(((InstructorActivity)getActivity()).modifiedCourse.getCode());
        textCourseName.setText(((InstructorActivity)getActivity()).modifiedCourse.getName());
        textCourseInstructor.setText(((InstructorActivity)getActivity()).modifiedCourse.getInstructor());

        editDescription.setText(((InstructorActivity)getActivity()).modifiedCourse.getDescription();
        editCapacity.setText(((InstructorActivity)getActivity()).modifiedCourse.getCapacity();
        editDays.setText(((InstructorActivity)getActivity()).modifiedCourse.getDays());
        editHours.setText(((InstructorActivity)getActivity()).modifiedCourse.getHours();

        if((((InstructorActivity)getActivity()).assignStatus == AssignStatus.ASSIGNABLE){
            assign.setText("Assign");
            assign.setVisibility(View.VISIBLE);
        }
        else if((((InstructorActivity)getActivity()).assignStatus == AssignStatus.UNASSIGNABLE){
            assign.setText("Unassign");
            assign.setVisibility(View.VISIBLE);
        }
        else{
            assign.setVisibility(View.GONE);
        }



        return view;
    }
}