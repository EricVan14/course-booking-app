package com.example.course_booking_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

//This class handles the database
public class DatabaseHandler extends SQLiteOpenHelper {

    //Initializations for Users table
    public static final String USER_TABLE_NAME = "users"; //Table name
    public static final String USER_PRIMARY_KEY = "usersID"; //Primary Key
    public static final String USER_COL_NAME = "username"; //First column name (user names)
    public static final String USER_COL_PASS = "password"; //Second column name (user passwords)
    public static final String USER_COL_TYPE = "userType"; //Third column name ("admin", "student" or "instructor")

    //Initializations for Courses table
    public static final String COURSE_TABLE_NAME = "courses"; //Table name
    public static final String COURSE_PRIMARY_KEY = "coursesID"; //Primary Key
    public static final String COURSE_COL_CODE = "courseCode"; //First column name (course codes)
    public static final String COURSE_COL_NAME = "courseName"; //Second column name (course names)
    public static final String COURSE_COL_INSTRUCTOR = "courseInstructor"; //Third column name (instructor names)
    public static final String COURSE_COL_DESCRIPTION = "courseDescription";
    public static final String COURSE_COL_CAPACITY = "courseCapacity";
    public static final String COURSE_COL_DAYS = "courseDays";
    public static final String COURSE_COL_HOURS = "courseHours";

    public DatabaseHandler(Context context){
        super(context, "users4.db", null, 5);
    }

    public DatabaseHandler(Context context, String name, int version){
        super(context, name, null, version);
    }

    @Override //"CREATE TABLE" Creates a table automagically when constructor is called
    public void onCreate(SQLiteDatabase db) {
        //Users table
        String createUsers = "CREATE TABLE " + USER_TABLE_NAME
                + "("
                + USER_PRIMARY_KEY + " INTEGER " + "PRIMARY KEY,"
                + USER_COL_NAME + " STRING, "
                + USER_COL_PASS + " STRING, "
                + USER_COL_TYPE + " STRING"
                + ")";

        //Courses table
        String createCourses = "CREATE TABLE " + COURSE_TABLE_NAME
                + "("
                + COURSE_PRIMARY_KEY + " INTEGER " + "PRIMARY KEY,"
                + COURSE_COL_CODE + " STRING, "
                + COURSE_COL_NAME + " STRING, "
                + COURSE_COL_INSTRUCTOR + " STRING, "
                + COURSE_COL_DESCRIPTION + " STRING, "
                + COURSE_COL_CAPACITY + " STRING, "
                + COURSE_COL_DAYS + " STRING, "
                + COURSE_COL_HOURS + " STRING"
                + ")";

        db.execSQL(createUsers);
        db.execSQL(createCourses);
    }

    public void addDefaults(){
        //Add preset users to the database
        this.addUser("admin", "admin123", "administrator");

        //add preset course
        this.addCourse("SEG2105Z", "Intro to Software Engineering", "Omar Badreddin");
    }

    @Override //"DROP" Removes both tables (never called?)
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String upgrade = "DROP TABLE IF EXISTS " + USER_TABLE_NAME;
        db.execSQL(upgrade);
        upgrade = "DROP TABLE IF EXISTS " + COURSE_TABLE_NAME;
        db.execSQL(upgrade);
        onCreate(db);
    }

    //Gets users in the form of ArrayList
    public ArrayList<User> getUsers(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorUsers = db.rawQuery("SELECT * FROM " + USER_TABLE_NAME, null);

        ArrayList<User> userArrayList = new ArrayList<>();

        if(cursorUsers.moveToFirst()){
            do{
                userArrayList.add(new User(
                    cursorUsers.getString(0),
                    cursorUsers.getString(1),
                    cursorUsers.getString(2),
                    cursorUsers.getString(3)
                ));
            } while(cursorUsers.moveToNext());
        }

        cursorUsers.close();
        return userArrayList;
    }

    //Gets courses in the form of ArrayList
    public ArrayList<Course> getCourses(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + COURSE_TABLE_NAME, null);

        ArrayList<Course> courseArrayList = new ArrayList<>();

        if(cursorCourses.moveToFirst()){
            do{
                courseArrayList.add(new Course(
                    cursorCourses.getString(0),
                    cursorCourses.getString(1),
                    cursorCourses.getString(2),
                    cursorCourses.getString(3),
                    cursorCourses.getString(4),
                    cursorCourses.getString(5),
                    cursorCourses.getString(6),
                    cursorCourses.getString(7)
                ));
            } while(cursorCourses.moveToNext());
        }

        cursorCourses.close();
        return courseArrayList;
    }

    //Adds a user to the Users table
    //Returns 0 if the user is successfully added, returns 2 if the account type has not been selected, returns 4 if the user already exists, returns 6 if another error occurs
    public int addUser(String username, String password, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (this.findPassword(username) == null && !type.equals("--Select account type for creation--")) { //cannot find a password associated with that user (the user doesn't yet exist)
            values.put(USER_COL_NAME, username);
            values.put(USER_COL_PASS, password);
            values.put(USER_COL_TYPE, type);
            db.insert(USER_TABLE_NAME, null, values);
            return 0;
        } else if (type.equals("--Select account type for creation--")){//Account type not selected yet
            return 2;
        } else if (this.findPassword(username) != null){//User already exists
            return 4;
        } else {
            return 6;
        }

    }


    //Adds a course to the courses table
    //Returns 0 if course was successfully added, returns 4 if course already exists, returns 8 if code or name is blank
    public int addCourse(String courseCode, String courseName, String courseInstructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (!this.courseExists(courseCode)) { //Course does not already exist
            values.put(COURSE_COL_CODE, courseCode);
            values.put(COURSE_COL_NAME, courseName);
            values.put(COURSE_COL_INSTRUCTOR, courseInstructor);
            values.put(COURSE_COL_CAPACITY, "0");
            db.insert(COURSE_TABLE_NAME, null, values);
            return 0;
        } else if (courseCode.equals("") || courseName.equals("")){
            return 8;
        } else {//Course already exists
            return 4;
        }
    }

    //Determines if a username and password match
    //Returns 0 if password and username match, returns 2 if user doesn't exist, returns 4 if user exists but pass is incorrect
    public int match(String username, String password){
        String foundPass = this.findPassword(username);//the password associated in the DB with username entered

        if (foundPass == null)//No password associated with this user, i.e. user doesn't exist
            return 2;
        else if (foundPass.equals(password)) //Password matches username
            return 0;
        else //User exists but password is incorrect
            return 4;

    }


    //modify a course
    //returns true if successful
    public boolean modifyCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        boolean result = false;

        values.put(COURSE_COL_CODE, course.getCode());
        values.put(COURSE_COL_NAME, course.getName());

        if(course.getInstructor() != null){
            values.put(COURSE_COL_INSTRUCTOR, course.getInstructor());
        }

        if(course.getDescription() != null){
            if(course.getDescription().length() > 0){
                values.put(COURSE_COL_DESCRIPTION, course.getDescription());
            }
        }

        if(course.getCapacity().length() > 0){
            values.put(COURSE_COL_CAPACITY, course.getCapacity());
        }

        if(course.getDays() != null){
            if(course.getDays().length() > 0){
                values.put(COURSE_COL_DAYS, course.getDays());
            }
        }

        if(course.getHours() != null){
            if(course.getHours().length() > 0){
                values.put(COURSE_COL_HOURS, course.getHours());
            }
        }

        db.update(COURSE_TABLE_NAME, values, COURSE_PRIMARY_KEY + "=?", new String[]{course.getID()});
        result = true;

        return result;

    }

    //Finds the password of a certain user
    private String findPassword(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        //SELECT "password" FROM "users" WHERE "username" = user
        String query = "SELECT " + USER_COL_PASS + " FROM " + USER_TABLE_NAME + " WHERE " + USER_COL_NAME + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(query, null);
        String foundPassword = null;

        if(cursor.moveToFirst()){//If password matches
            foundPassword = cursor.getString(0);
        }

        cursor.close();
        //If user doesn't match any in database, foundPassword will be null
        return foundPassword;
    }

    //Determines if a certain course exists
    public boolean courseExists(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        //SELECT "courseName" FROM "users" WHERE "courseCode" = code
        String query = "SELECT " + COURSE_COL_NAME + " FROM " + COURSE_TABLE_NAME + " WHERE " + COURSE_COL_CODE + " = \"" + code + "\"";

        Cursor cursor = db.rawQuery(query, null);
        String found = null;

        if(cursor.moveToFirst())//If code matches a code in the database
            found = cursor.getString(0);

        cursor.close();

        //If user doesn't match any in database, foundPassword will be null
        if (found == null)
            return false;
        else
            return true;
    }

    //Finds the type of a certain user
    public String findUserType(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        //SELECT "userType" FROM "users" WHERE "username" = user
        String query = "SELECT " + USER_COL_TYPE + " FROM " + USER_TABLE_NAME + " WHERE " + USER_COL_NAME + " = \"" + user + "\"";

        Cursor cursor = db.rawQuery(query, null);
        String foundType = null;

        if(cursor.moveToFirst()){//If password matches
            foundType = cursor.getString(0);
        }

        cursor.close();

        //If user doesn't match any in database, foundType will be null
        return foundType;
    }

    //Public method deleting a user from the database. Returns true if successfully deleted.
    //NOTE: currently no password needed to delete an account
    public boolean removeUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        boolean result = false;

        //SELECT * FROM users WHERE "username" = username
        String query = "SELECT * FROM "+ USER_TABLE_NAME + " WHERE " + USER_COL_NAME + " = \"" + username + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            result = true;
            db.delete(USER_TABLE_NAME, USER_COL_NAME + "=?", new String[]{username});
            cursor.close();
        }

        return result;
    }

    //Public method deleting a course from the database. Returns true if successfully deleted.
    public boolean removeCourse(String courseID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        boolean result = false;

        //SELECT * FROM courses WHERE "courseName" = courseID
        String query = "SELECT * FROM "+ COURSE_TABLE_NAME + " WHERE " + COURSE_PRIMARY_KEY + " = \"" + courseID + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            result = true;
            db.delete(COURSE_TABLE_NAME, COURSE_PRIMARY_KEY + "=?", new String[]{courseID});
            cursor.close();
        }
        return result;
    }

}