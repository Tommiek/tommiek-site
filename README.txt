== Introductie ==

Dit is de root directory van de Tommiek SiteGenerator! Deze 
tool genereerd de hele website op basis van een statische
site en een model van categorien en producten.

== Quickstart ==

0) Bewerk producten en categorien
1) java -jar generator\tommiek-sitegenerator-0.0.1-SNAPSHOT.jar
2) FTP target directory naar live

== Directories ==

  * categories	- categorien metadata
  * products	- producten metatdate
  * static	- de statische basis site
  * templates	- de template paginas
  * generator	- de Tommiek SiteGenerator
  * target	- de gegenereerde site


== Producten onderhouden ==

In de map products heeft elk product een eigen directory 
waarin een product, bestaande uit metadata en plaatjes, staat.
De metadata is een xml file en moet .xml als extensie hebben. 
De naam van de file is arbitrair, maar wellicht handig om 
deze gelijk te houden aan de product id. 

Zie: products/_product-template.xml


== Categorien onderhouden ==

In de map categories heeft elke category een eigen 
directory waarin een category, bestaande uit metadata en 
plaatjes, staat. De metadata is een xml file en moet .xml als 
extensie hebben. De naam van de file is arbitrair, maar 
wellicht handig om deze gelijk te houden aan de product id.
Tevens bepaald de volgorder van de category directories de
volgorde waarin ze op de portolio pagina verschijnen.


Zie: products/_category-template.xml


Todo:

1) pixels
2) site.xml
3) meer seo
  
