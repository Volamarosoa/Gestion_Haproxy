scp /home/rota/Documents/backend1.cfg rota@localhost:/etc/haproxy/conf.d
sudo -S systemctl restart haproxy
# sudo chmod 777 /home/rota/ITU/Mr_Naina/Cluster/fichier/*.war 
# cd WEB-INF/classes
# java -cp .:../lib/* main.Main