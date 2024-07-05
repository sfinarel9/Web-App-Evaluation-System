-- My checkout 
SELECT *
FROM ergasia2.log
WHERE (tDateAndTime between '2020-05-15 13:18:26' and  '2020-05-15 13:22:10') ;-- AND aRemoteIPAddress = '94.66.56.39' ;

-- 1. Τον αριθμό των εγγραφών στα log files.

SELECT COUNT(*)
FROM ergasia2.log ;

-- 2. Τον αριθμό των διαφορετικών browsers που επισκέφτηκαν το e-shop.

SELECT COUNT( DISTINCT  agent) as DifferentAgents
FROM ergasia2.log ;

-- 3. Το ποσοστό των κινήσεων ανά browser.

SELECT  agent , COUNT(*)
FROM ergasia2.log 
GROUP BY agent;

-- 4. Να βρεθούν όλοι οι εξωτερικοί referrers.

SELECT referer as 'external referer'
FROM ergasia2.log
WHERE referer NOT LIKE '%datalab.stef.teicrete.gr:8780%' 
GROUP BY referer;

-- 5. Να υπολογιστεί ο συνολικός αριθμός bytes που διακινήθηκε από τον web server για την εξυπηρέτηση των δικών σας συνδιαλλαγών.

SELECT sum(bBytesSent)
FROM ergasia2.log
WHERE (tDateAndTime between '2020-05-15 13:18:26' and  '2020-05-15 13:22:10')  AND aRemoteIPAddress = '94.66.56.39' ;

-- 6. Πόσες γραμμές δημιουργήθηκαν από JMeter.

SELECT  count(*)
FROM ergasia2.log
WHERE agent  LIKE 'Apache%' ;

-- 7. Να βρεθούν όλα τα requests που οδήγησαν σε σφάλμα 404, αν υπάρχουν.

SELECT URequestedURLPath
FROM ergasia2.log 
WHERE sHTTPStatusCode = '404' ;