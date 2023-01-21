# QiwiPay

![](https://img.shields.io/bstats/servers/12879?style=for-the-badge&logo=appveyor) ![](https://img.shields.io/bstats/players/12879?style=for-the-badge&logo=appveyor)

Плагин для MineCraft, позволяющий проводить платежи через QIWI p2p API.

### Возможности

- Приём платежей через p2p QIWI API (оплата с киви-кошельков и банковских карт);
- Сохранение всех платежей в базу данных;
- Система промокодов на фиксированные суммы и на процент к пополнению

### Преимущества

![](https://img.shields.io/codefactor/grade/github/PukPukov/QiwiPay?style=for-the-badge&logo=appveyor) ![](https://img.shields.io/codeclimate/maintainability-percentage/PukPukov/qiwipay?style=for-the-badge&logo=appveyor)

- Отсутствие каких-либо комиссий на переводы;
- Чистый и расширяемый код (качество кода 100% согласно codefactor.io, уровень поддерживаемости - 83% согласно CodeClimate)
- Проект полностью OpenSource (лицензия MIT)

# QiwiPay

![](/META-INF/qiwipay222.png)

![](https://img.shields.io/github/tag/pukpukov/qiwipay?style=for-the-badge&logo=appveyor)

![](https://img.shields.io/github/issues/pukpukov/qiwipay?style=for-the-badge&logo=appveyor) ![](https://img.shields.io/tokei/lines/github/pukpukov/qiwipay?style=for-the-badge&logo=appveyor)

## Настройка плагина
Чтобы плагин работал, необходимо ввести секретный ключ от p2p qiwi api в QiwiConfiguration.yml. Получить его можно тут: https://p2p.qiwi.com/.
Также надо заполнить все позиции ниже
![](/META-INF/pic2.png)

## Приём платежей
Для создания счёта об оплате игрок должен ввести /dn pay <сумма>. После того, как он оплатит, он ещё должен подтвердить платёж командой /dn check. Если счёт будет оплачен, в консоль будет выпущена команда, указанная в конфигурации, с суммой, которую заплатил игрок.

## Использование
С помощью QiwiPay можно продавать внутриигровую валюту, а за нее, например, продавать платные привилегии, по крайней мере, я это использую именно так

## Промокоды
В плагине можно создавать промокоды на пополнение и на фиксированные выплаты. Делал это для своего сервера MineCraft для новогоднего ивента, поэтому пока что сообщения не настриваются

#### Создание

/promo create fixed <sum> <количество_использований> <срок_действия в днях> - создать промокод с фиксированным вознаграждением

/promo create bonus <bonus> <колво использований> <срок действия в днях> - создать промокод с бонусом на пополнение (бонус указывается в процентах, он добавляется к сумме пополнения, например бонус 50 даст 5 рублей к пополнению в 10 рублей). Обратите внимание, что все промокоды поднимаются в верхний регистр, то есть nEwYear2022 превратится в NEWYEAR2022
  
#### Использование
  
Игроки могут использовать промокоды с фиксированным вознаграждением командой /promo use <название>.

Для того, чтобы получить бонус к пополнению, надо в команде /dn pay <сумма> в конце ещё дописать промокод, например /dn pay 50 NEWYEAR2022

*QiwiPay является форком EdenPay.*
