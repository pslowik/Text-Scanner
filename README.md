# Text Scanner
1. Opis i technologie

Aplikacja mParagon służy do odczytu tekstu ze zdjęcia, np. paragonu, wykonanego w aplikacji, przeglądania już wykonanych skanów oraz ich usuwania.
Została napisana w środowisku Android Studio z wykorzystaniem języka Java. Mechanizm odczytu tekstu ze zrobionego w czasie rzeczywistym zdjęcia wykorzystuje pakiet ML Firebase. Dane przechowywane są w Firebase Realtime Database, a zdjęcia są przechowywane w Firebase Storage.
  
2. Budowa

Struktura aplikacji oparta jest na 3 głównych klasach obsługujących Aktywności oraz dwóch pomocniczych.

Główne:
- MainActivity – obsługuje cały proces OCR – od zrobienia zdjęcia, poprzez odczyt tekstu ze zdjęcia, aż po jego wyświetlenie wraz ze zdjęciem.
- Paragon – obsługuje listę wykonanych dotąd odczytów paragonów, wyszukiwanie oraz ograniczanie wyświetlania według podanych dat. Dane pobierane są odpowiednio z Firebase Realtime Database oraz Firebase Storage).
- DetailsActivity – obsługuje wyświetlanie pełnych informacji zapisanych w Firebase (zdjęcie wraz z OCR) oraz ich usuwanie.

Pomocnicze
- ParagonData – określa obiekt, który przechowuje dane paragonu.
  - data (data wykonania)
  - name (nazwa)
  - imageurl (ścieżka dostępu do zdjęcia w Firebase Storage)
  - key (id zawierające klucz i wartość danych Firebase Realtime Database)
• UploadInfo – określa obiekt, który przechowuje dane potrzebne do zapisu danych do bazy Firebase Realtime Database.
  - imageName – tekst OCR 
  - imageURL – ścieżka pliku

3. Działanie

MainActivity:
Po uruchomieniu aplikacji ukazuje się widok czterech przycisków:
- Snap – służy do wykonania zdjęcia tekstu (zdjęcia należy robić w orientacji pionowej).
- Detect – po naciśnięciu aplikacja odczytuje tekst ze zrobionego wcześniej zdjęcia (nie działa bez zrobionego wcześniej zdjęcia).
- Add – dodaje zdjęcie i odczytany tekst do bazy danych.
- Recepits – wyświetla na ekranie listę wykonanych skanów.

Paragon:
Tutaj wyświetlane są wykonane wcześniej skany. Użytkownik może wyszukiwać tekstowo:
- w polu „Search here...” – pasujące wyniki są wyświetlane w czasie rzeczywistym,
- według daty utworzenia, wpisując datę według podanego wzoru:  ROK(4 cyfry)-MIESIĄC(2 cyfry)-DZIEŃ(2 cyfry),
„od” w polu „from yyyy-MM-dd” oraz „do” w kolejnym polu „to yyyy-MM-dd”. Operację należy zatwierdzić przyciskiem „Filter Date”.
W celu wyświetlenia pełnych danych skanu (całości tekstu i zdjęcia) należy kliknąć w wybrany element listy, o czym przypomina komunikat na samym dole ekranu.
Przycisk „Back” służy do powrotu do powrotu do ekranu skanowania.
     
DetailsActivity2:
Wyświetla pełne dane zdjęcie wraz z odczytanym z niego tekstem.
Za pomocą przycisków:
- „Back” użytkownik wróci do listy (w tym momencie kasowane są atrybuty
wyszukiwania tekstowego i według daty).
- „Delete” – usunie wyświetlany skan z listy i bazy danych.
