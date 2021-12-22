package main

import (
	"net/http"
	"time"
	"log"
	"encoding/json"
	"io/ioutil"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/gorilla/handlers"

	"github.com/jmoiron/sqlx"
	_ "github.com/mattn/go-sqlite3"
)

func flashcardSetter(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	flashcard := flashcard{}

	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		log.Printf("Err; %v", err)
		http.Error(w, `{"error": "body error"}`, http.StatusNotFound)
		return
	}
	err = json.Unmarshal(body, &flashcard)
	if err != nil {
		log.Printf("Err; %v", err)
		http.Error(w, `{"error": "Unmarshal"}`, http.StatusNotFound)
		return
	}

	flashcard.Id, _ = strconv.Atoi(vars["id"])

	_, err = DB.NamedExec("UPDATE flashcard SET recto=:recto, verso=:verso WHERE id=:id", flashcard)
	if err != nil {
		log.Printf("Err; %v", err)
		http.Error(w, `{"error": "Not Found"}`, http.StatusNotFound)
		return
	}

	w.Write([]byte("{}"))
}

func flashcardGetter(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	flashcards := []flashcard{}
	if err := DB.Select(&flashcards, "SELECT * FROM flashcard WHERE id = ?", vars["id"]); err != nil {
		log.Printf("Err; %v", err)
		http.Error(w, `{"error": "Not Found"}`, http.StatusNotFound)
		return
	}

	if len(flashcards) == 0 {
		http.Error(w, `{"error": "Not Found"}`, http.StatusNotFound)
		return
	}

	jsonRes, err := json.Marshal(flashcards[0])
	if err != nil {
		log.Printf("Err getting flashcard %v", err)
	}

	w.Write(jsonRes)
}

func flashcardLister(w http.ResponseWriter, r *http.Request) {
	flashcards := []flashcard{}
	if err := DB.Select(&flashcards, "SELECT * FROM flashcard"); err != nil {
		log.Printf("Err; %v", err)
	}

	jsonRes, err := json.Marshal(flashcards)
	if err != nil {
		log.Printf("Err listing flashcards %v", err)
	}

	w.Write(jsonRes)
}

var DB *sqlx.DB

var schema string = `
  CREATE TABLE IF NOT EXISTS flashcard (
    recto TEXT,
    verso TEXT,
    id INTEGER PRIMARY KEY);`

type flashcard struct {
	Recto string `db:"recto",json:"recto"`
	Verso string `db:"verso",json:"verso"`
	Id int `db:"id",json:"id"`
}

func main() {
	// Open DB
	db, err := sqlx.Connect("sqlite3", "db.sqlite3")
	if err != nil {
		log.Fatal(err)
	}
	DB = db

	// Apply schema/migrations
	db.MustExec(schema);

	r := mux.NewRouter()
	r.Handle("/static/", http.FileServer(http.Dir("static/")))
	r.HandleFunc("/api/flashcard/{id:[0-9]+}", flashcardGetter).Methods("GET")
	r.HandleFunc("/api/flashcard/{id:[0-9]+}", flashcardSetter).Methods("POST")
	r.HandleFunc("/api/flashcard", flashcardLister).Methods("GET")

	log.Println("Listening ...")
	srv := &http.Server{
		Handler: handlers.CORS()(r),
		Addr: "localhost:8000",
		WriteTimeout: 15 * time.Second,
		ReadTimeout: 15 * time.Second,
	}
	log.Fatal(srv.ListenAndServe())
}
