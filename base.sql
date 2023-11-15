-- ~/Documents/apache-tomcat-10.0.22/bin$ sudo ./catalina.sh start
-- http://localhost:8082/

create database cluster;
\c cluster;

create table serveur (
    id serial,
    ip varchar(20),
    nom varchar(20),
    etat int
);

create database cluster;
\c cluster;

create table serveur (
    id serial primary key,
    ip varchar(20),
    nom varchar(20),
    path varchar(255),
    port int,
    etat int
);




git clone https://github.com/Volamarosoa/FrontServlet.git /home/rota/ITU/Projets/Git/FrontServlet
git clone https://github.com/Volamarosoa/GestionEmploye.git /home/rota/ITU/Projets/Git/GestionEmploye

defaults
	log	global
	mode	http
	option	httplog
	option	dontlognull
        timeout connect 5000
        timeout client  50000
        timeout server  50000
	errorfile 400 /etc/haproxy/errors/400.http
	errorfile 403 /etc/haproxy/errors/403.http
	errorfile 408 /etc/haproxy/errors/408.http
	errorfile 500 /etc/haproxy/errors/500.http
	errorfile 502 /etc/haproxy/errors/502.http
	errorfile 503 /etc/haproxy/errors/503.http
	errorfile 504 /etc/haproxy/errors/504.http

global
    log /dev/log local0
    log /dev/log local1 notice
    chroot /var/lib/haproxy
#    stats socket /run/haproxy/admin.sock mode 660 level admin expose-fd listeners
    stats timeout 30s
    user haproxy
    group haproxy
#    daemon

frontend myfrontend
    bind *:81
    mode http
    default_backend mybackend

backend mybackend

sudo chown nouvel_utilisateur:nouveau_groupe /etc/haproxy/haproxy