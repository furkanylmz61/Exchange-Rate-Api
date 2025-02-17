# Exchange Rate API Dokümantasyonu

Bu proje, TCMB (Türkiye Cumhuriyet Merkez Bankası) üzerinden **döviz kurları**nı çekerek kullanıcıların belirli para birimleri arasındaki dönüşümleri yapmalarını sağlayan basit bir Spring Boot uygulamasıdır. Uygulama, her saat başı **TCMB’nin today.xml** verisini çekerek günceller ve hafızada tutar.

## İçindekiler
1. [Proje Yapısı](#proje-yapisi)
2. [Çalıştırma](#calistirma)
3. [REST Endpoint’leri](#rest-endpointleri)
    - [Tüm Döviz Kurlarını Getir](#tum-doviz-kurlarini-getir)
    - [Tek Bir Para Biriminin Kurunu Getir](#tek-bir-para-biriminin-kurunu-getir)
    - [Dönüşüm (Convert)](#donusum-convert)
4. [Planlanmış Görev (Scheduling)](#planlanmis-gorev-scheduling)
5. [Örnek Yanıtlar](#ornek-yanitlar)
6. [Hata Yönetimi](#hata-yonetimi)

---

## Proje Yapısı

```
com.furkanylmz.exchange_rate_api
├── controller
│   └── CurrencyRateController.java   // API uç noktalarını (endpoints) içerir.
├── exception
│   └── GlobalExceptionHandler.java   // Uygulama bazlı hata yönetimi.
├── model
│   └── CurrencyRate.java             // Döviz kuru bilgilerini tutan model sınıfı.
├── services
│   └── CurrencyRateService.java      // TCMB verisini çekip rates isimli Map'e atar.
└── ExchangeRateApiApplication.java   // Ana uygulama sınıfı (Spring Boot).
```

**Önemli Sınıflar:**
- **`CurrencyRateService`**:
    - `fetchRates()` metodu ile TCMB’nin `today.xml` verisini çekip parse eder.
    - `getRates()` metodu ile güncel döviz kurlarını bir `Map` yapısında döndürür.
- **`ExchangeRateApiApplication`**:
    - Uygulamanın ana sınıfıdır.
    - `@EnableScheduling` anotasyonu sayesinde planlanmış görevler çalışır.
    - `@Scheduled(cron = "0 0 * * * *")` her saat başı kurları otomatik günceller.

---

## Çalıştırma

1. **Projenin Bağımlılıklarını İndirin:**  
   Maven veya Gradle kullanıyorsanız, ilgili bağımlılıkları (Spring Boot, Lombok, Swagger vb.) indirin.

2. **TCMB Kurları için İnternet Erişimi:**  
   Proje, TCMB’nin “`https://www.tcmb.gov.tr/kurlar/today.xml`” adresinden veri çektiği için, internet bağlantınızın olması gerekir.

3. **Uygulamayı Başlatın:**
   ```bash
   mvn spring-boot:run
   ```
   Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışmaya başlayacaktır.

4. **Swagger Kullanımı (Opsiyonel):**  
   Eğer Swagger/OpenAPI UI kuruluysa, `http://localhost:8080/swagger-ui/index.html` adresinden API dokümantasyonuna ulaşabilirsiniz.

---

## REST Endpoint’leri

### Tüm Döviz Kurlarını Getir
- **Endpoint**: `GET /api/latest`
- **Açıklama**: TCMB’den çekilmiş ve servis katmanında saklanan **tüm döviz kurlarını** JSON formatında döndürür.
- **Örnek İstek**:
  ```http
  GET /api/latest
  ```
- **Örnek Yanıt**:
  ```json
  {
    "USD": {
      "currencyCode": "USD",
      "forexBuying": 27.0429,
      "forexSelling": 27.0923
    },
    "EUR": {
      "currencyCode": "EUR",
      "forexBuying": 29.6734,
      "forexSelling": 29.7268
    },
    ...
  }
  ```

### Tek Bir Para Biriminin Kurunu Getir
- **Endpoint**: `GET /api/latest/{currencyCode}`
- **Açıklama**: İlgili `currencyCode` parametresine göre **tek bir** döviz kurunu döndürür.
- **Örnek İstek**:
  ```http
  GET /api/latest/EUR
  ```
- **Örnek Yanıt**:
  ```json
  {
    "currencyCode": "EUR",
    "forexBuying": 29.6734,
    "forexSelling": 29.7268
  }
  ```

### Dönüşüm (Convert)
- **Endpoint**: `GET /api/convert`
- **Parametreler**:
    - `from`: Kaynak para birimi (Ör: `EUR`)
    - `to`: Hedef para birimi (Ör: `USD`)
    - `amount`: Dönüştürülmek istenen miktar (Ör: `100`)
- **Açıklama**: Sadece **EUR** veya **GBP** para biriminden **USD**'ye dönüşüm yapılır. Diğer para birimleri istek yapılırsa **RuntimeException** fırlatılır.
- **Örnek İstek**:
  ```http
  GET /api/convert?from=EUR&to=USD&amount=100
  ```
- **Örnek Yanıt**:
  ```json
  {
    "from": "EUR",
    "to": "USD",
    "amount": 100,
    "convertedAmount": 110.75
  }
  ```
- **İşleyiş Mantığı**:
  ```java
  // Örnek hesaplama mantığı:
  convertedAmount = amount * (USD'nin ForexSelling'i / EUR'un ForexSelling'i);
  ```

---

## Planlanmış Görev (Scheduling)

Uygulama içinde tanımlı `@Scheduled(cron = "0 0 * * * *")` her saat başı `currencyRateService.fetchRates()` metodunu çağırarak güncel kurları otomatik olarak çekmektedir.

```java
@Scheduled(cron = "0 0 * * * *")
public void scheduleRateUpdate() {
    currencyRateService.fetchRates();
}
```

- Böylece uygulama restart edilmeden de güncel verilerle çalışmaya devam eder.
- **`@PostConstruct`** anotasyonu ile de uygulama ilk ayağa kalktığında `fetchRates()` metodu çağırılır ve kurlar yüklenmiş olur.

---

## Örnek Yanıtlar

**1. Tüm Kurları Getirme**
```http
GET /api/latest
```
```json
{
  "USD": {
    "currencyCode": "USD",
    "forexBuying": 27.0429,
    "forexSelling": 27.0923
  },
  "EUR": {
    "currencyCode": "EUR",
    "forexBuying": 29.6734,
    "forexSelling": 29.7268
  }
}
```

**2. Belirli Kur**
```http
GET /api/latest/GBP
```
```json
{
  "currencyCode": "GBP",
  "forexBuying": 34.6058,
  "forexSelling": 34.6875
}
```

**3. EUR → USD Dönüşüm**
```http
GET /api/convert?from=EUR&to=USD&amount=50
```
```json
{
  "from": "EUR",
  "to": "USD",
  "amount": 50,
  "convertedAmount": 46.37
}
```

**4. Hata Durumu (EUR ve GBP dışında)**
```http
GET /api/convert?from=TRY&to=USD&amount=100
```
```json
{
  "error": "Sadece EUR ve GBP USD'ye dönüştürülebilir."
}
```

---

## Hata Yönetimi

- **GlobalExceptionHandler** sınıfı içerisinde `RuntimeException` tipi hatalar yakalanır ve basit bir JSON çıktı döndürülür.
- Hata mesajı `"error"` anahtarı altında döner:
  ```json
  {
    "error": "Sadece EUR ve GBP USD'ye dönüştürülebilir."
  }
  ```

Bu sayede kullanıcıya okunabilir ve tutarlı hata mesajları göstermek kolaylaşır.

---

# Sonuç
- **Exchange Rate API**, Spring Boot kullanarak geliştirilen bir döviz dönüştürme servisidir.
- Uygulama, TCMB’nin sağladığı XML verisini parse ederek `CurrencyRateService` üzerinde saklar.
- Belirli aralıklarla (her saat) veriler güncellenir.
- Kullanıcılara sunduğu endpoint’lerle, **EUR** ve **GBP** para birimlerinden **USD**'ye hızlıca dönüşüm yapmanıza imkân tanır.
- İhtiyaç olması hâlinde `convertCurrency` metodunun genişletilmesi ya da ek endpoint’lerle daha farklı senaryolar (örn: EUR → GBP, USD → EUR vb.) da kolayca gerçekleştirilebilir.

