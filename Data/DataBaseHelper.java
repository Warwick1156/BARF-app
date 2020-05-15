package Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Foods.Component;
import Foods.Food;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "barf.db";
    public static final String DBLOCATION = "C:\\Users\\techw\\AndroidStudioProjects\\BARF_API_25\\app\\src\\main\\java\\Data";
    private Context context;
    private SQLiteDatabase db;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DBNAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDataBase() {
        String dbPath = context.getDatabasePath(DBNAME).getPath();
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (Exception e) {
            System.out.println("Error occurred during opening database");
        }
    }

    public void closeDatabase() {
        try {
            db.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred during closing database");
        }
    }

    public List<Food> getFoods(String name, String type) {
        List<Food> foodList = new ArrayList<>();

        String query = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                List<Component> componentList = new ArrayList<>();
                String foodName = cursor.getString(0);
                String foodAnimal = cursor.getString(1);
                String foodType = cursor.getString(2);

                for (int i = 3; i <= cursor.getCount(); i++) {
                    Component.Id id = Component.getId(cursor.getColumnName(i));
                    float value = cursor.getFloat(i);

                    Component component = new Component(id, value);
                    componentList.add(component);
                }

                Food food = new Food(foodName, foodAnimal, foodType, componentList);
                foodList.add(food);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }
}
