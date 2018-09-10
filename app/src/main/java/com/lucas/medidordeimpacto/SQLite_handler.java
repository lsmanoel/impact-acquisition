package com.example.lucassearamanoel.atividade_cadastrodecarros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lucas.medidordeimpacto.Book;

import java.util.LinkedList;
import java.util.List;

public class SQLite_handler extends SQLiteOpenHelper {
    //----------------------------------------------------------------------------------------------
    // nome da tabela
    private static final String TABLE_BOOKS = "books";

    // nome das colunas da tabela
    private static final String KEY_ID = "id";
    private static final String KEY_1 = "carroModelo";
    private static final String KEY_2 = "carroValor";
    private static final String[] COLUMNS = {KEY_ID, KEY_1,KEY_2};

    // Versão do banco de dados
    private static final int DATABASE_VERSION = 1;
    // nome do banco
    private static final String DATABASE_NAME = "BookDB";

    public SQLite_handler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase db) {

        // concatenacao de commando SQL para criacao das tabelas
        String CREATE_BOOK_TABLE =
            "CREATE TABLE " + TABLE_BOOKS + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_1 + " TEXT, " +
                    KEY_2 + " NUM )";

        // cria a tabela
        db.execSQL(CREATE_BOOK_TABLE);//<-Executa o script SQL
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // se houver outra tabela com o mesmo nome
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        // criar nova tabela
        this.onCreate(db);
    }

    //----------------------------------------------------------------------------------------------
    public void addBook(Book book){
        Log.d("addBook", book.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Log.d("step 1", "-----------------------------------------------------------------");

        values.put(KEY_1, book.get_modeloCarro()); // get title
        values.put(KEY_2, book.get_valorCarro()); // get author

        //Log.d("step 2", "-----------------------------------------------------------------");

        db.insert(TABLE_BOOKS, // tabela
                null, //nullColumnHack
                values); // chave + valor do campo

        //Log.d("step 3", "-----------------------------------------------------------------");

        db.close();

        //Log.d("step 4", "-----------------------------------------------------------------");
    }

    public Book getBook(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        // commando SQL de leitura
        Cursor cursor =db.query(TABLE_BOOKS, COLUMNS, " id = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Book book = new Book();
        book.setId(Integer.parseInt(cursor.getString(0)));
        book.set_position(cursor.getString(1));
        book.set_mean_acceleration(cursor.getString(2));
        Log.d("getBook("+id+")", book.toString());
        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new LinkedList<Book>();
        // comando SQL
        String query = "SELECT * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Book book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new Book();
                book.setId(Integer.parseInt(cursor.getString(0)));
                book.set_position(cursor.getString(1));
                book.set_mean_acceleration(cursor.getString(2));
                books.add(book);
            } while (cursor.moveToNext());
        }
        Log.d("getAllBooks()", books.toString());
        return books;
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_1, book.get_position()); // get title
        values.put(KEY_2, book.get_mean_acceleration()); // get author
        int i = db.update(TABLE_BOOKS, values, KEY_ID+" = ?", new String[] { String.valueOf(book.getId()) });
        db.close();
        return i;
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, KEY_ID+" = ?", new String[] { String.valueOf(book.getId()) });
        db.close();
        Log.d("deleteBook", book.toString());
    }
}
