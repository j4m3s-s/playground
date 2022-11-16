package main

import (
	// Logging / string formating
	"fmt"
	"log"

	// Configuration
	"os"

	// network
	"net/http"
	"io/ioutil"

	"encoding/json"

	// for network payload
	"bytes"
)

func main() {

	// Login button page
	http.HandleFunc("/", rootHandler)

	// Redirect to GH
	http.HandleFunc("/login/github/", githubLoginHandler)

	// Callback
	http.HandleFunc("/login/github/callback", githubCallbackHandler)

	http.ListenAndServe(":8080", nil)

}

func githubCallbackHandler(w http.ResponseWriter, r *http.Request) {
	// url param
	code := r.URL.Query().Get("code")

	githubAccessToken := getGithubAccessToken(code)

	githubData := getGithubData(githubAccessToken)

	loggedInHandler(w, r, githubData)
}

func loggedInHandler(w http.ResponseWriter, r *http.Request, data string) {
	if len(data) == 0 {
		fmt.Fprintf(w, "Not authorized")
		return
	}

	w.Header().Set("Content-Type", "application/json")

	var prettyJSON bytes.Buffer
	pretty_parsed := json.Indent(&prettyJSON, []byte(data), "", "\t")
	if pretty_parsed != nil {
		log.Panic("JSON parse error")
	}

	fmt.Fprintf(w, string(prettyJSON.Bytes()))
}

func getGithubData(accessToken string) string {
	req, reqerr := http.NewRequest("GET", "https://api.github.com/user", nil)
	if reqerr != nil {
		log.Panic("API Request creation failed")
	}

	authorizationHeaderValue := fmt.Sprintf("token %s", accessToken)
	req.Header.Set("Authorization", authorizationHeaderValue)

	resp, resperr := http.DefaultClient.Do(req)
	if resperr != nil {
		log.Panic("Request failed")
	}

	respbody, _ := ioutil.ReadAll(resp.Body)

	return string(respbody)
}

func getGithubAccessToken(code string) string {

	clientID, ok := os.LookupEnv("CLIENT_ID")
	if !ok {
		log.Fatal("Missing CLIENT_ID")
	}
	clientSecret, ok := os.LookupEnv("CLIENT_SECRET")

	requestBodyMap := map[string]string{"client_id": clientID, "client_secret": clientSecret, "code": code}
	requestJSON, _ := json.Marshal(requestBodyMap)

	req, reqerr := http.NewRequest("POST", "https://github.com/login/oauth/access_token", bytes.NewBuffer(requestJSON))
	if reqerr != nil {
		log.Panic("Request creation failed")
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")

	resp, resperr := http.DefaultClient.Do(req)
	if resperr != nil {
		log.Panic("Request failed")
	}

	respbody, _ := ioutil.ReadAll(resp.Body)

	// Represents the response received from Github
	type githubAccessTokenResponse struct {
		AccessToken string `json:"access_token"`
		TokenType   string `json:"token_type"`
		Scope       string `json:"scope"`
	}

	var ghresp githubAccessTokenResponse
	json.Unmarshal(respbody, &ghresp)

	return ghresp.AccessToken
}

func rootHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, `<a href="/login/github/">LOGIN </a>`)
}

func githubLoginHandler(w http.ResponseWriter, r *http.Request) {
	githubClientId, ok := os.LookupEnv("CLIENT_ID")
	if !ok {
		log.Panic("No CLIENT_ID defined")
	}

	redirectURL := fmt.Sprintf("https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s",
		githubClientId,
		"http://localhost:8080/login/github/callback",
	)

	http.Redirect(w, r, redirectURL, 301)
}
