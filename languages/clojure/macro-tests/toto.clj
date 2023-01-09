; To launch w/ babashka. Too lazy to create a whole project just for a tiny
; experiment.

; Note that this doesn't support multiple args, it's just a proof of concept for
; unwrap / early return style for "option" types. We use a convention akin to
; Rust's one, while changing it by PRE-pending the variable since appending '?'
; is common in Clojure to signify functions that return booleans.
(defmacro ?let
  [arg1 arg2 & remains]
  (if (clojure.string/includes? arg1 "?")
    (if (nil? arg2)
      (throw (Exception. "BILLION DOLLAR MISTAKE"))
      `(let [toto ~arg2]
         ~@remains))))

(?let ?toto nil
      (println user/toto))

