//xamp, go, android studio
//http://localhost:8000/api/items
package main

import (
	"database/sql"
	"encoding/json"
	"log"
	"fmt"
	"math/rand"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

type Item struct {
	Item_name string  `json:"item_name"`
	Weight    float64 `json:"weight"`
	Count     int     `json:"count"`
}

type Journey struct {
	Journey_name string `json:"journey_name"`
}

func main() {
	fmt.Println("starting API")
	r := mux.NewRouter()

	r.HandleFunc("/api/journeys", getJourneys).Methods("GET")
	r.HandleFunc("/api/journeys/generate", generateJourney).Methods("GET")
	r.HandleFunc("/api/items", getItems).Methods("GET")
	r.HandleFunc("/api/items/generate", generateItemsList).Methods("GET")

	fmt.Println("listening")
	log.Fatal(http.ListenAndServe(":8000", r))
	fmt.Println("listening")
}

func generateItemsList(w http.ResponseWriter, r *http.Request) {
	items := getItemsFromDb()
	var generatedList []Item
	minItems := 5
	itemsCount := rand.Intn(len(items)-minItems) + minItems

	exist := func(num int) bool {
		for i := 0; i < len(generatedList); i++ {
			if generatedList[i] == items[num] {
				return true
			}
		}
		return false
	}

	for i := 0; i < itemsCount; i++ {
		randNumber := -1
		for {
			randNumber = rand.Intn(len(items))
			existing := exist(randNumber)
			if !existing {
				break
			}
		}
		if items[randNumber].Count > 1 {
			items[randNumber].Count = rand.Intn(items[randNumber].Count) + 1
		}		
		generatedList = append(generatedList, items[randNumber])
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(generatedList)
}

func generateJourney(w http.ResponseWriter, r *http.Request) {
	journeys := getJourneysFromDb()
	randNumber := rand.Intn(len(journeys))
	generatedJ := journeys[randNumber]
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(generatedJ)
}

func getItems(w http.ResponseWriter, r *http.Request) {
	items := getItemsFromDb()
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(items)
}

func getJourneys(w http.ResponseWriter, r *http.Request) {
	journeys := getJourneysFromDb()
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(journeys)
}

func getItemsFromDb() []Item {
	db, err := sql.Open("mysql", "root:@tcp(localhost)/android")
	if err != nil {
		panic(err)
	}
	// fmt.Println("connected to db")
	defer db.Close()

	var items []Item

	rows, err := db.Query("select * from items")
	if err != nil {
		panic(err)
	}

	for rows.Next() {
		var item Item
		err = rows.Scan(&item.Item_name, &item.Weight, &item.Count)
		if err != nil {
			panic(err)
		}
		items = append(items, item)
	}

	return items
}

func getJourneysFromDb() []Journey {
	db, err := sql.Open("mysql", "root:@tcp(localhost)/android")
	if err != nil {
		panic(err)
	}
	// fmt.Println("connected to db")
	defer db.Close()

	var journeys []Journey

	rows, err := db.Query("select * from journeys")
	if err != nil {
		panic(err)
	}

	for rows.Next() {
		var journey Journey
		err = rows.Scan(&journey.Journey_name)
		if err != nil {
			panic(err)
		}
		journeys = append(journeys, journey)
	}

	return journeys
}
