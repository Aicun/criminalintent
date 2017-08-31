package lac.com.crimimalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lac.com.crimimalintent.database.CrimeBaseHelper;
import lac.com.crimimalintent.database.CrimeCursorWrapper;
import lac.com.crimimalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by Aicun on 8/17/2017.
 */

public class CrimeManager {
    private static CrimeManager sInstance;
    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeManager getInstance(Context context) {
        if(sInstance == null) {
            synchronized (CrimeManager.class) {
                if(sInstance == null) {
                    sInstance = new CrimeManager(context);
                }
            }
        }
        return sInstance;
    }

    private CrimeManager(Context context){
        //use application context instead of activity
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public List<Crime> getmCrimes() {
        List<Crime> crimeList = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = queryCrimes(null,null);
        try {
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()) {
                Crime crime = cursorWrapper.getCrime();
                crimeList.add(crime);
                cursorWrapper.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursorWrapper.close();
        }
        return crimeList;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try {
            if(cursorWrapper.getCount() ==0 )return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursorWrapper.close();
        }
        return null;
    }

    public void addCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME,null,contentValues);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getmId().toString();
        ContentValues contentValues = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,contentValues,CrimeTable.Cols.UUID + " = ?",new String[]{uuidString});
    }

    public void deleteCrime(Crime crime) {
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?", new String[]{crime.getmId().toString()});
    }

    public File getCrimePhoto(Crime crime) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null) return null;
        return new File(externalFilesDir,crime.getCrimePhotoName());
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID,crime.getmId().toString());
        contentValues.put(CrimeTable.Cols.TITLE,crime.getmTitle());
        contentValues.put(CrimeTable.Cols.DATE,crime.getmDate().toString());
        contentValues.put(CrimeTable.Cols.SOLVED,crime.ismSolved()?1:0);
        contentValues.put(CrimeTable.Cols.SUSPECT,crime.getmSuspect());
        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] args) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME,null,whereClause,args,null,null,null);
        return new CrimeCursorWrapper(cursor);
    }
}
