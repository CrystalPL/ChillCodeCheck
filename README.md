# Sprawdzanie

Plugin dodający możliwość sprawdzenia gracza pod kątem niedozwolonego oprogramowania.

Do działania wtyczki wymagany jest plugin: **[CrCAPI](https://github.com/CrystalPL/CrCAPI/releases/)**.
___

### Instalacja

Plugin przeznaczony jest dla wersji MC od 1.8 do 1.18.1. Działa z dowolnym silnikiem opartym o BukkitAPI — CraftBukkit,
Spigot, Paper, Tuinity itp.
___

### Konfiguracja (opisane do logów sprawdzania)

W pliku config.yml znajdują się ustawienia do połączenia z bazą danych. Baza danych jest wymagana, by trzymać informacje
o przebiegu sprawdzania. Przechowywać te dane można w: SQLite, MySQL lub w MongoDB.

W ustawieniach znajduję się pole prefix, podaję się tam przedrostek, jaki ma występować w nazwach tabel (SQLite, MYSQL)
lub kolekcji (MongoDB).

___

### Komendy

W pluginie jest jedna komenda /check, z aliasem /sprawdz. Komenda, jak i alias jest możliwa do zmiany w pliku
konfiguracyjnym. Przed użyciem komendy, gracz musi mieć uprawnienie **chillcode.check.base**.

Jeżeli gracz ma uprawnienie **chillcode.check.bypass**, to nie może być sprawdzany.

| Komenda                    | Uprawnienie               | Opis komendy                                          |
|----------------------------|---------------------------|-------------------------------------------------------|
| /check help                | chillcode.check.help      | Wyświetla wszystkie dostępne komendy                  |
| /check setspawn            | chillcode.check.setspawn  | Ustawia miejsce teleportu graczy podczas sprawdzania  |
| /check spawn               | chillcode.check.spawn     | Teleportuje na wyznaczone miejsce sprawdzania         |
| /check sprawdz \<gracz>    | chillcode.check.sprawdz   | Rozpoczyna sprawdzanie danego gracza                  |
| /check przyznanie \<gracz> | chillcode.check.admitting | Przyznanie się do cheatów, wykonuje komendy z configu |
| /check czysty \<gracz>     | chillcode.check.clear     | Nie wykryto cheatów                                   |
| /check cheater \<gracz>    | chillcode.check.cheater   | Wykrycie cheatów, wykonuje komendy z configu          |
| /check logs \<gracz>       | chillcode.check.logs      | Ponowne załadowanie ustawień pluginu                  |
| /check reload              | chillcode.check.reload    | Daty i przebieg sprawdzania gracza                    |

Plugin początkowo stworzony pod projekt: **chillcode**.
