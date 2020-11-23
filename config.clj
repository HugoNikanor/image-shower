;;; Example configuration for image-shower.
;;;
;;; Will be looked for in $XDG_CONFIG_HOME/image-shower/config.clj,
;;; followed by /etc/image-shower/config.clj. XDG_CONFIG_HOME falls
;;; back to $HOME/.config.
;;;
;;; Note that this is NOT a clojure file, but will be passed
;;; through clojures read.
;;; Biggest thing to note is that symbols should NOT be quoted.

{
 ;; http://korma.github.io/Korma/korma.db.html#var-default-connection
 ;; db-args are passod along to create-db
 :db-type sqlite3
 :db-args { :db "image-shower.db" }

 ;; :db-type postgres
 ;; :db-args {:db "image-shower" :user "hugo"}

 :port 3000
 :host "0.0.0.0"

 ;; where static content (images and the like) can be found
 :data-path "public/"
 }
