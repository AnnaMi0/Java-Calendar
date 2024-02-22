# <ins>Αντικειμενοστραφής Προγραμματισμός 2 | εργασία "Καλεντάρι" </ins>

### [HTTPS link to repository](https://github.com/AnnaMi0/OOP2-Project.git) (private)

## <ins>Απαιτούμενα</ins>

- [Java JDK 17 LTS](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi) (χρησιμοποιήθηκε η έκδοση 3.8.1)

## <ins>Οδηγίες Εκτέλεσης</ins>

<ol>
<li>Μεταβείτε στον φάκελο του project:</li>

Το directory στο οποίο βρίσκεστε πρέπει να έχει την μορφή `/.../OOP2ndPart`,
πχ: `~/Documents/OOP2ndPart`


```bash
cd /.../OOP2ndPart
```

<li>Το extraction του εκτελέσιμου γίνεται με την εξής εντολή:</li> 

```bash 
mvn package
```

<li>Έπειτα για να τρέξετε το εκτελέσιμο στο τερματικό του linux τρέξτε την παρακάτω εντολή:</li>

```bash
java -jar target/OOP2ndPart-1.0-SNAPSHOT.jar
```
</ol>

## <ins> Σημαντικές Πληροφορίες - Αποφάσεις </ins>

### <ins> Κληρονομικότητα: </ins>
<p>
Όσο αναφορά τα events, υπάρχουν 3 κλάσεις και 1 interface. Η υπέρκλαση myEvent (που περιλαμβάνει no-time events και απλά events)
και οι υπόλοιπες δύο υποκλάσεις της myEvent, η Project που αντιστοιχεί σε μια εργασία(task/todo) και 
η Appointment που αντιστοιχεί σε ραντεβού. Όλα συνδέονται μεταξύ τους μέσω του interface και υλοποιούν κάποιες κοινές μεθόδους
(που χρησιμοποιείται για συλλογή πολλών event σε LinkedList).
</p>

### <ins> Είδη event: </ins>
<p>
Πέραν από τα ραντεβού και τις εργασίες που ορίζονται στην εκφώνηση, αποφασίσαμε να προσθέσουμε και άλλες δύο κατηγορίες
ώστε να χάνονται όσο το δυνατόν λιγότερα event κατά την διαδικασία του loading ενός αρχείου ics. Οπότε όλες οι κατηγορίες event είναι οι εξής:

- no-time event: δεν περιέχουν χρόνο ούτε διάρκεια ή πέρας.
- event: περιέχουν χρόνο αλλά δεν περιέχουν διάρκεια ή πέρας.
- Appointment: δόθηκε από την εκφώνηση, περιέχει διάρκεια και χρόνο.
- Project: δόθηκε από την εκφώνηση, αντιστοιχεί σε task και περιλαμβάνει χρόνο και κατάσταση ολοκλήρωσης.

Οι δύο νέες κατηγορίες (event, no-time event) συμπεριλαμβάνονται στην κλάση myEvent, και διαχωρίζονται μέσω της μεταβλητής hasTime
και τη χρήση του αντίστοιχου constructor.

> Σημείωση: Ο χρήστης μπορεί να προσθέσει και να επεξεργαστεί μόνο Appointment και Project.

</p>

### <ins>Loading αρχείου ICS και διαχωρισμός των ειδών event:</ins>
<p>
Σε περίπτωση που έχουμε <ins>VTODO</ins> τα πράγματα είναι απλά. Αγνοείται το DTSTART και ως προθεσμία της εργασίας παίρνουμε 
το DTEND ή DUE. Αν κάποιο από αυτά τα πεδία δεν έχει χρόνο τότε βάζουμε ως ώρα τα μεσάνυχτα (00:00) και δημιουργείται 
αντικείμενο Project με της πληροφορίες του vtodo.

Στην περίπτωση που έχουμε <ins>VEVENT</ins> πρώτα ελέγχεται αν έχει πεδίο DURATION ή DTEND. Αν έχει, και τo DTSTART δεν έχει
χρόνο τότε βάζουμε ως ώρα 00:00 και το αποθηκεύουμε ως Appointment, ενώ αν έχει χρόνο το DTSTART τότε απλά αποθηκεύεται με την
ώρα που ορίζει αυτό. Αν δεν υπάρχουν πεδία DTEND και DURATION τότε το αποθηκεύουμε ως απλό event ή no-time event αναλόγα αν έχει
το DTSTART χρόνο.
</p>

### <ins>Calendars - Bonus</ins>
<p>
Αποτελεί μια κλάση που ομαδοποιεί αντικείμενα myCalendar και προσθέτει κάποιες επιπλέον λειτουργίες. Για αρχή, μεταφέρθηκε
εδώ η <b>presentEvents</b> (η οποία ανάλογα το κουμπί -all,day,week κτλ- που θα πατήσει ο χρήστης επιστρέφει μια λίστα με myEvent).
Επιπλέον σε αυτή την κλάση γίνεται <b>sorting</b> των event χρησιμοποιώντας έναν custom comparator που συγκρίνει αντικείμενα του eventsInterface.
Επίσης η κλάση αυτή είναι υπέθυνη για τις <b>ειδοποιήσεις</b> που λαμβάνει ο χρήστης.
</p>

### <ins>Ειδοποιήσεις</ins>
<p>
Η μέθοδος notifyUser καλείται μέσω thread στην κλάση Windows και εκτελείται κάθε δευτερόλεπτο. 
Για κάθε event σε όλα τα ημερολόγια που έχει επιλέξει ο χρήστης (μέσω του φίλτρου), αν το starting time ενός event είναι ανάμεσα σε 30 και 
15 λεπτά ή 15 μέχρι και 0 λεπτό ο χρήστης λαμβάνει ένα notification. Επομένως αν ένα event αρχίζει σε 25 λεπτά απο τώρα, θα λάβει 
μια ειδοποίηση τώρα και άλλη μία σε 10 λεπτά (όταν δηλαδή θα είναι 15 λεπτά πριν αρχίσει).


</p>

### <ins>Sorting</ins>
<p>
Μεταξύ των calendar γίνεται sorting μέσω του custom comparator όπως αναφέρθηκε και πριν, στην κλάση Calendars.
</p>

### <ins>Logs</ins>
<p>
Για την απόκρυψη περιττόν logs του ical4j κατά το loading ενώς αρχείου ics φτιάξαμε το αρχείο <code>logback.xml </code>
που βρίσκεται στο src/main/resources και φτιάξαμε configuration που να αποκρύπτει αυτά τα logs.
</p>

### <ins>Προσωρινό αντικείμενο myEvent</ins>
<p>
Κατά την προσθήκη ενός event, παίρνουμε input από τον χρήστη για τα κοινά πεδία μεταξύ των Appointment και 
Project και με αυτά φτιάχνουμε ένα προσωρινό αντικείμενο myEvent το οποίο χρησιμοποιείται από τις μεθόδους add 
του Appointment και Project ώστε να δημιουργηθεί το αντίστοιχο αντικείμενο. Επιλέξαμε να το κάνουμε αυτό ώστε να μην είναι copy-paste
οι μέθοδοι add όσον αναφορά τα κοινά πεδία.
</p>


## Παραθυρική Εφαρμογή

<p>Δημιουργήσαμε 5 νέες κλάσεις για την παραθυρική εφαρμογή:</p>
<ol>
<li>CalendarButton</li>
<li>Windows</li>
<li>TimeChooser</li>
<li>Customs</li>
<li>EventAdder</li>
</ol>


### <ins>TimeChooser</ins>
<p> Χειρίζεται το input χρόνου του χρήστη από την παραθυρική εφαρμογή.</p>

### <ins>Customs</ins>
<p>Κλάση που μορφοποιεί το παράθυρο.</p>


### <ins>Windows</ins>
<p>Το κύριο παράθυρο του προγράμματός μας πάνω στο οποίο «χτίζονται» όλες οι υπόλοιπες εξωτερικές λειτουργίες που ζητήθηκαν στην εκφώνηση. Αναλυτικότερα, ξεκινάμε από ένα bar το οποίο περιέχει επιλογές:

- File->Περιέχει την Choose a File όπου ο χρήστης εισάγει αρχείο, την About και την Exit για την έξοδο του χρήστη από το πρόγραμμα (είναι ισοδύναμη της επιλογής του κόκκινου κουμπιού X)

- ![bell_icon.png](src%2Fmain%2Fresources%2Fbell_icon.png) -> Icon το οποίο εμφανίζεται μόνο όταν υπάρχουν προγραμματισμένα ραντεβού σε διάστημα 30 λεπτών, και περιλαμβάνει
μια λίστα με αυτά τα event.

- ![filter.png](src%2Fmain%2Fresources%2Ffilter.png) -> Icon το οποίο «φιλτράρει» τα ημερολόγια του χρήστη, δηλαδή του δίνει επιλογές για το ποια επιθυμεί να εμφανίζονται.

- Κουμπί + ->Δημιουργία ενός νέου Event.


Ύστερα, φεύγοντας από το bar, στην αρχική οθόνη υπάρχει ο τωρινός χρόνος ανανεωμένος ανά δευτερόλεπτο.

Όταν ο χρήστης φορτώσει ένα αρχείο, εμφανίζονται οι τίτλοι των events και τα κουμπιά για την ανάλογη ταξινόμησή τους.
Οι τίτλοι έχουν την επιλογή να επιλεχθούν με αριστερό κλικ για να **προβληθούν οι λεπτομέρειές τους** και, αφότου σε κάθε περίπτωση έχουμε δει 
τις πληροφορίες ενός event, μπορούν να επιλεχθούν και με δεξί κλικ προκειμένου να κάνουμε **edit** το επιλεγμένο event.
Τα κουμπιά εμφανίζουν τα events ταξινομημένα όπως έχει ζητηθεί, από τα σημερινά events μέχρι και τα due events.
</p>

### <ins>EventAdder</ins>
<p>
Κλάση υπεύθυνη για την προσθήκη ενός event
</p>

### <ins>CalendarButton</ins>
<p>
Κλάση που χειρίζεται τα κουμπία All,Day,Week etc και τυπώνει τα αντίστοιχα event για κάθε πάτημα κουμπιού.
</p>

### <ins>Main Class</ins>
<p>Η κλάση main πλέον είναι υπεύθυνη μόνο για τη δημιουργία του κυρίως παραθύρου (parent)</p>



## <ins>Oμάδα</ins>
<li>Άννα Μιχαηλίδου (it2022066)</li>

<li>Ιωάννης Λυμπερόπουλος (it2022059)</li>

<li>Βασίλειος Κοτούμπας (it2022044)</li>



## <ins>Βιβλιοθήκες</ins>
<p> Οι βιβλιοθήκες που χρησιμοποιούνται είναι οι εξής:</p>
<ol>
<li>LocalDateTime</li>
<li>LinkedList</li>
<li>java.time</li>
<li>net.fortuna.ical4j</li>
<li>ch.qos.logback.classic</li>
<li>gr.hua.dit.oop2.calendar</li>
<li>org.slf4j.LoggerFactory</li>
<li>java.nio.file</li>
<li>java.io</li>
<li>javax.swing</li>
<li>java.awt</li>
<li>org.jdatepicker.impl.JDatePanelImpl</li>
<li>org.jdatepicker.impl.UtilDateModell</li>
<li>java.util</li>
</ol>




## <ins> Πηγές </ins>


- [iCalendar.org](https://icalendar.org/)
- [ical4j.github.io](https://ical4j.github.io/)
- [ChatGPT](https://chat.openai.com/)
- [StackOverflow](https://stackoverflow.com/)
- [Github docs (αρχείο MD)](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax)
- [daringfireball.net](https://daringfireball.net/projects/markdown/) (αρχείο MD)
- [ical.marudot.com](https://ical.marudot.com/)
- bingchat
- Deitel-Java Προγραμματισμός 10η έκδοση