# PowerShell Script to generate 100+ Page PDF Document for WORK Project
$htmlPath = "C:\Users\hp\AndroidStudioProjects\WORK\project_documentation_100p.html"
$pdfRoot = "C:\Users\hp\AndroidStudioProjects\WORK\project_documentation.pdf"
$pdfArtifact = "C:\Users\hp\AppData\Local\Google\AndroidStudio2026.1.3\projects\work.add98449\.artifacts\11aeda2d-f8da-4168-bec7-96aa35d20b56\project_documentation.pdf"

# Read Java source files
$mainActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\MainActivity.java" -Raw
$roleSelectionActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\RoleSelectionActivity.java" -Raw
$clientMainActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ClientMainActivity.java" -Raw
$providerMainActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ProviderMainActivity.java" -Raw
$searchActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\SearchActivity.java" -Raw
$addServiceActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\AddServiceActivity.java" -Raw
$serviceDetailActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ServiceDetailActivity.java" -Raw
$chatActivity = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ChatActivity.java" -Raw
$serviceRepository = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ServiceRepository.java" -Raw
$orderRepository = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\OrderRepository.java" -Raw
$firebaseHelper = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\FirebaseHelper.java" -Raw
$searchHistoryManager = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\SearchHistoryManager.java" -Raw
$serviceItem = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ServiceItem.java" -Raw
$orderItem = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\OrderItem.java" -Raw
$chatMessage = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ChatMessage.java" -Raw
$serviceAdapter = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\ServiceAdapter.java" -Raw
$orderAdapter = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\OrderAdapter.java" -Raw
$messageAdapter = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\java\com\example\work\MessageAdapter.java" -Raw

# Read XML files
$actMainXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_main.xml" -Raw
$actRoleXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_role_selection.xml" -Raw
$actClientXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_client_main.xml" -Raw
$actProviderXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_provider_main.xml" -Raw
$actSearchXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_search.xml" -Raw
$actAddServiceXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_add_service.xml" -Raw
$actDetailXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_service_detail.xml" -Raw
$actChatXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\activity_chat.xml" -Raw
$itemServiceXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\item_service.xml" -Raw
$itemOrderXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\item_order.xml" -Raw
$itemChatXml = Get-Content "C:\Users\hp\AndroidStudioProjects\WORK\app\src\main\res\layout\item_chat_message.xml" -Raw

function Escape-Html($str) {
    if ($null -eq $str) { return "" }
    return [System.Web.HttpUtility]::HtmlEncode($str)
}

# Add Web assembly reference for HttpUtility
Add-Type -AssemblyName System.Web

# Build huge HTML
$sb = [System.Text.StringBuilder]::new()

$null = $sb.AppendLine(@"
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
<meta charset="UTF-8">
<title>التوثيق المرجعي والهندسي الشامل لتطبيق شُغل WORK</title>
<style>
    @import url('https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800&family=Tajawal:wght@400;500;700;800&display=swap');

    @page {
        size: A4;
        margin: 22mm 18mm 22mm 18mm;
    }

    body {
        font-family: 'Tajawal', 'Cairo', 'Segoe UI', Tahoma, sans-serif;
        color: #0f172a;
        background-color: #ffffff;
        line-height: 1.85;
        font-size: 13pt;
        margin: 0;
        padding: 0;
        text-align: justify;
    }

    @media print {
        .page-break { page-break-before: always; }
        body { background-color: #ffffff; }
    }

    .title-page {
        height: 92vh;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        align-items: center;
        text-align: center;
        page-break-after: always;
        padding: 40px 20px;
        border: 10px double #0f172a;
        box-sizing: border-box;
        background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
    }

    .main-title {
        font-family: 'Cairo', sans-serif;
        font-size: 34pt;
        font-weight: 800;
        color: #0f172a;
        margin: 10px 0;
    }

    .sub-title {
        font-size: 19pt;
        font-weight: 700;
        color: #ea580c;
        margin-bottom: 25px;
    }

    .meta-box {
        background-color: #f1f5f9;
        border-right: 8px solid #ea580c;
        padding: 25px 35px;
        border-radius: 10px;
        width: 85%;
        text-align: right;
        margin: 25px 0;
    }

    .meta-item {
        font-size: 14pt;
        margin: 12px 0;
        color: #1e293b;
    }

    .dedication-box {
        background-color: #fff7ed;
        border: 2px solid #ffedd5;
        border-radius: 12px;
        padding: 20px 30px;
        font-style: italic;
        color: #9a3412;
        font-size: 13.5pt;
        width: 85%;
        line-height: 1.9;
    }

    h1 {
        font-family: 'Cairo', sans-serif;
        font-size: 24pt;
        font-weight: 800;
        color: #0f172a;
        border-bottom: 4px solid #ea580c;
        padding-bottom: 10px;
        margin-top: 40px;
        margin-bottom: 25px;
    }

    h2 {
        font-family: 'Cairo', sans-serif;
        font-size: 18pt;
        font-weight: 700;
        color: #1e293b;
        background-color: #f8fafc;
        padding: 10px 16px;
        border-right: 6px solid #ea580c;
        border-radius: 0 8px 8px 0;
        margin-top: 30px;
        margin-bottom: 20px;
    }

    h3 {
        font-size: 15pt;
        font-weight: 700;
        color: #ea580c;
        margin-top: 25px;
        margin-bottom: 12px;
    }

    h4 {
        font-size: 13.5pt;
        font-weight: 700;
        color: #0f172a;
        margin-top: 18px;
        margin-bottom: 8px;
    }

    p {
        margin-bottom: 14px;
        text-indent: 18px;
    }

    ul, ol {
        margin-bottom: 18px;
        padding-right: 28px;
    }

    li {
        margin-bottom: 8px;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin: 25px 0;
        font-size: 12pt;
    }

    th, td {
        border: 1px solid #cbd5e1;
        padding: 12px 14px;
        text-align: right;
    }

    th {
        background-color: #0f172a;
        color: #ffffff;
        font-weight: 700;
    }

    tr:nth-child(even) { background-color: #f8fafc; }

    pre {
        background-color: #0f172a;
        color: #f8fafc;
        padding: 18px;
        border-radius: 10px;
        overflow-x: auto;
        font-size: 10.5pt;
        line-height: 1.55;
        border-left: 6px solid #ea580c;
        margin: 22px 0;
        white-space: pre-wrap;
        word-break: break-all;
        direction: ltr;
        text-align: left;
        font-family: 'Consolas', 'Courier New', monospace;
    }

    .note-box {
        background-color: #eff6ff;
        border-right: 6px solid #2563eb;
        padding: 18px 22px;
        border-radius: 8px;
        margin: 22px 0;
        font-size: 13pt;
    }

    .diagram-box {
        background-color: #f8fafc;
        border: 2px dashed #94a3b8;
        border-radius: 12px;
        padding: 22px;
        text-align: center;
        margin: 25px 0;
        font-family: monospace;
        direction: ltr;
        font-size: 11pt;
    }
</style>
</head>
<body>

<!-- COVER PAGE -->
<div class="title-page">
    <div>
        <div style="font-size: 16pt; font-weight: 700; color: #0f172a;">جامعة الأقسام الهندسية والتكنولوجية</div>
        <div style="font-size: 14pt; color: #475569; margin-top: 5px;">كلية تكنولوجيا المعلومات والفيزياء البرمجية - قسم هندسة البرمجيات والتطبيقات الذكية</div>
        <div style="font-size: 52pt; color: #ea580c; margin: 20px 0;">🛠️ WORK</div>
        <div class="main-title">مشروع التوثيق الهندسي والأكاديمي الشامل<br>لتطبيق "شُغل - WORK"</div>
        <div class="sub-title">منصة سحابية تفاعلية بالوقت الفعلي لسوق الخدمات المصغرة والمهن الحرة</div>
    </div>

    <div class="meta-box">
        <div class="meta-item"><strong>اسم المشروع:</strong> تطبيق شُغل (WORK App)</div>
        <div class="meta-item"><strong>إعداد الطالب:</strong> إنجاز وتطوير تنفيذي وبرمجي فردي متكامل</div>
        <div class="meta-item"><strong>إشراف وتوجيه الأستاذ الفاضل:</strong> أستاذ علي</div>
        <div class="meta-item"><strong>المساق الدراسي:</strong> تطوير تطبيقات الهواتف الذكية المتقدمة (Advanced Android App Development)</div>
        <div class="meta-item"><strong>التقنيات المستعملة:</strong> Android SDK (Java 11), Firebase Realtime Database, Firebase Auth, Material Design 3, JSON Serialization, Base64 Image Compression</div>
        <div class="meta-item"><strong>سنة التقديم:</strong> الأكاديمي 2026م</div>
    </div>

    <div class="dedication-box">
        "أرفع أسمى آيات الشكر والتقدير والامتنان لأستاذي الفاضل <strong>أستاذ علي</strong> على جهوده الكبيرة، وتوجيهاته العلمية السديدة، ومتابعته الدائمة لكافة مراحل هذا المشروع من مرحلة التصميم حتى الربط السحابي والاختبار النهائي، والتي كان لها الفضل الأول بعد الله في إخراج هذا العمل بهذه الجودة والاحترافية العالية."
    </div>
</div>

<div class="page-break"></div>
<h1>📋 فهرس المحتويات والأنشطة الهندسية</h1>
<table>
    <thead>
        <tr>
            <th>رقم الفصل</th>
            <th>عنوان الفصل والمبحث التفصيلي</th>
            <th>نطاق التغطية والمحتوى</th>
        </tr>
    </thead>
    <tbody>
        <tr><td><strong>الفصل الأول</strong></td><td>المقدمة الشاملة وخلفية المشكلة والدراسة الأولية</td><td>أهداف المشروع، الحاجة الماسة، والحلول المبتكرة المضافة.</td></tr>
        <tr><td><strong>الفصل الثاني</strong></td><td>الدراسات السابقة والتحليل المقارن الميداني</td><td>تحليل تطبيقات خمسات، Fiverr، Upwork، ومقارنة الميزات.</td></tr>
        <tr><td><strong>الفصل الثالث</strong></td><td>تحليل المتطلبات الوظيفية وحالات الاستخدام (Use Cases)</td><td>مخطط حالات الاستخدام، سيناريوهات العمل للعميل والمزود.</td></tr>
        <tr><td><strong>الفصل الرابع</strong></td><td>المعمارية الهندسية الهيكلية وأنماط التصميم البرمجي</td><td>Repository Pattern, Singleton, Clean Architecture, Components.</td></tr>
        <tr><td><strong>الفصل الخامس</strong></td><td>هياكل البيانات والخوارزميات المبتكرة والتحليل التعقيدي</td><td>Smart Price Proximity Algorithm & Base64 Compression Algorithm.</td></tr>
        <tr><td><strong>الفصل السادس</strong></td><td>التكامل السحابي المباشر مع خوادم Google Firebase</td><td>Realtime Database NoSQL, Anonymous Authentication, Regional URLs.</td></tr>
        <tr><td><strong>الفصل السابع</strong></td><td>المخططات الهندسية المتقدمة (UML Class & Sequence Diagrams)</td><td>مخطط الصفوف البرمجية الشامل ومخطط تتابع البيانات.</td></tr>
        <tr><td><strong>الفصل الثامن</strong></td><td>التصميم البصري ونظام واجهات المستخدم Material Design 3</td><td>شرح ملفات الـ XML والأنماط البصرية والألوان.</td></tr>
        <tr><td><strong>الفصل التاسع</strong></td><td>دليل الاستخدام التشغيلي والكتالوج المصور (User Manual)</td><td>دليل خطوة بخطوة للعملاء ومقدمي الخدمات وطريقة الشات.</td></tr>
        <tr><td><strong>الفصل العاشر</strong></td><td>قواعد البيانات الشجرية السحابية NoSQL بالتفصيل الممل</td><td>تحليل العُقد <code>/services</code>, <code>/orders</code>, <code>/chats</code>.</td></tr>
        <tr><td><strong>الفصل الحادي عشر</strong></td><td>التوثيق البرمجي القياسي القياسي (JavaDocs)</td><td>توثيق الفئات والدوال والبارامترات القياسية لـ JavaDoc.</td></tr>
        <tr><td><strong>الفصل الثاني عشر</strong></td><td>أثر المشروع العلمي والتقييم الذاتي والمراجع (APA)</td><td>الملخص التعلمي، المهارات المكتسبة، المراجع المعتمدة.</td></tr>
        <tr><td><strong>الملحق (أ)</strong></td><td>الأكواد المصدرية الكاملة للواجهات والجافا (Java Source Code)</td><td>جميع صوف الجافا البالغ عددها 18 ملفاً كودياً بنصها الكامل.</td></tr>
        <tr><td><strong>الملحق (ب)</strong></td><td>الأكواد المصدرية الكاملة للتصميم والـ XML Layouts</td><td>جميع ملفات التنسيق البصري والواجهات بنصها الكامل.</td></tr>
    </tbody>
</table>

<div class="page-break"></div>
<h1>الفصل الأول: المقدمة الشاملة وخلفية المشكلة والجدوى الفنية</h1>

<h2>1.1 مقدمة ونظرة عامة على المشروع</h2>
<p>شهد العالم في السنوات الأخيرة تحولاً جذرياً نحو التكنولوجيا الرقمية والعمل الحر المستقل (Freelancing)، حيث أصبح أصحاب المهارات التقنية واليدوية يفضلون أسلوب تقديم الخدمات المصغرة والمهنية الحرة التي توفر لهم المرونة والاستقلالية المالية. مع هذا التطور، أصبحت الهواتف الذكية بنظام الأندرويد هي الوسيلة الرئيسية التي يستخدمها ملايين الأفراد يومياً للتواصل وإنجاز الأعمال والتسوق والبحث عن الخدمات.</p>
<p>يقدم هذا التقرير هندسة وتوثيقاً شاملاً لتطبيق الأندرويد السحابي <strong>"شُغل - WORK"</strong>، وهو منصة برمجية متكاملة مصممة بلغة Java ومربوطة بسحابة Google Firebase Realtime Database لتوفير بيئة تفاعلية وسريعة تجمع بين <strong>العميل (Client)</strong> الراغب في الحصول على خدمة مصغرة أو مهنية، و<strong>مقدم الخدمة (Provider)</strong> أصحاب المهارات الراغبين في عرض خدماتهم وتحقيق دخل مادي.</p>

<h2>1.2 خلفية المشروع ومن أين جاءت الفكرة</h2>
<p>نشأت فكرة تطبيق <strong>"شُغل - WORK"</strong> من ملاحظة ميدانية دقيقة للمشاكل التي يواجهها الشباب والطلاب والمهنيون أصحاب المهارات (مثل مصممي الجرافيك، مبرمجي التطبيقات والمواقع، المترجمين، أخصائيي التسويق الرقمي، وفنيي الصيانة). يواجه هؤلاء الأفراد صعوبة كبيرة في إيجاد وسيلة سهلة ومباشرة لعرض مهاراتهم للجمهور المحلي، مع تحملهم لتكاليف تسويق عالية أو عمولات باهظة تفرضها المنصات التقليدية.</p>
<p>في المقابل، يواجه العميل المباشر صعوبة في العثور على صاحب مهارة موثوق وسريع التجاوب يتناسب مع السعر والميزانية المحددة لديه. من هنا، جاءت الفكرة لبناء تطبيق أندرويد سريع ومباشر بأسلوب Low Friction يتيح للعميل والمزود الدخول المباشر واختيار الدور، والبحث الذكي بالميزانية، والمزامنة الحية المباشرة للخدمات والطلبات، والدردشة الفورية داخل التطبيق وخيارات التواصل المباشر عبر الواتساب والمكالمات.</p>

<h2>1.3 دراسة أولية والحلول المبتكرة المضافة</h2>
<p>قبل الشروع في كتابة الكود البرمجي، تم إجراء دراسة مسحية واستكشافية لسوق تطبيقات الخدمات. وُجد أن غالبية التطبيقات الحالية تعتمد على أنظمة سكونية (Static) أو واجهات ويب معقدة وتطلب تسجيل حسابات معقدة. وبناءً عليه، تم الابتكار والتطوير في مشروعنا ليغطي الثغرات التالية:</p>

<div class="note-box">
    <strong>💡 الابتكارات والإضافات البرمجية المعتمدة في تطبيق "شُغل":</strong>
    <ul>
        <li><strong>خوارزمية المطابقة الذكية بأسعار الميزانية (Smart Price Proximity Matching Algorithm):</strong> تستقبل الميزانية بالدولار ($) وتحسب نسبة التقارب والانحراف في السعر مع ترتيب النتائج وعرض شارات توضيحية مرئية للعميل.</li>
        <li><strong>نظام المزامنة اللحظية ثنائية الاتجاه (Real-Time Multi-Device Sync):</strong> الربط السحابي المباشر بـ Firebase Realtime Database بحيث يظهر أي عنصر جديد أو تحديث حالة فوراً لدى جميع الأجهزة بالوقت الفعلي.</li>
        <li><strong>نظام المحادثات والدردشة المباشرة داخل التطبيق (In-App Realtime Chat System):</strong> توفير شات سحابي متكامل يربط الطرفين داخل التطبيق مع حفظ وتزامن الرسائل بالوقت الفعلي.</li>
        <li><strong>خوارزمية ضغط وتحويل الصور إلى Base64 String:</strong> تقليص أبعاد الصور وتقليل حجمها وتشفيرها لـ JPEG Base64 لمشاركتها سحابياً بسرعة جبارة.</li>
    </ul>
</div>

<h2>1.4 التحديات البرمجية والهندسية التي واجهتني أثناء المشروع</h2>
<ol>
    <li><strong>تعارض إضافات Gradle وحل مشكلة المكررات:</strong> معالجة خطأ تكرار استدعاء <code>com.android.application</code> بتنظيف هيكلية `build.gradle.kts`.</li>
    <li><strong>عدم تطابق اسم الحزمة (Package Name Mismatch):</strong> معالجة تعارض `google-services.json` بتوحيد اسم الحزمة إلى <code>com.example.work</code>.</li>
    <li><strong>تعدد نطاقات عناوين الفايربيس السحابية:</strong> بناء صف <code>FirebaseHelper</code> الذكي للربط التلقائي بالنطاقات المختلفة (US, EU, Asia).</li>
    <li><strong>مشكلة التخزين السحابي والتغلب على القواعد المغلقة:</strong> إضافة معالجة الأخطاء الشفافة للتنبيه بقواعد الأمان وتفعيل التسجيل المجهول التلقائي <code>signInAnonymously()</code>.</li>
</ol>

<div class="page-break"></div>
<h1>الفصل الثاني: الدراسات السابقة والتحليل المقارن الميداني</h1>

<h2>2.1 مراجعة المنصات القائمة وتحليلها</h2>
<p>تم فحص وتقييم أبرز التطبيقات والمنصات العالمية والعربية القائمة في مجال العمل الحر والخدمات المصغرة:</p>

<table>
    <thead>
        <tr>
            <th>وجه المقارنة</th>
            <th>منصة خمسات (Khamsat)</th>
            <th>تطبيق Fiverr</th>
            <th>منصة مستقل / Upwork</th>
            <th>تطبيق شُغل (WORK App)</th>
        </tr>
    </thead>
    <tbody>
        <tr><td><strong>سرعة التواصل</strong></td><td>بطيئة (تعتمد على الرسائل الإلكترونية)</td><td>متوسطة</td><td>بطيئة (عقود طويلة)</td><td><strong>فورية لحظية (شات داخلي + واتساب مباشر)</strong></td></tr>
        <tr><td><strong>البحث السعري</strong></td><td>ثابت أو فئات غير مرنة</td><td>فئات سعرية محددة</td><td>عروض ومزايدات معقدة</td><td><strong>بحث تقريبي بالميزانية الذكية (Proximity Matching)</strong></td></tr>
        <tr><td><strong>المزامنة الحية</strong></td><td>يحتاج إعادة تحميل الصفحة</td><td>مزامنة جيدة عبر السيرفر</td><td>تحديثات دورية</td><td><strong>مزامنة حية لحظية Realtime ثنائية الاتجاه</strong></td></tr>
        <tr><td><strong>سهولة الاستخدام</strong></td><td>متوسطة</td><td>معقدة المزايا</td><td>معقدة للمشاريع الضخمة</td><td><strong>بسيطة وسريعة جداً (Low Friction UX)</strong></td></tr>
        <tr><td><strong>معالجة الصور</strong></td><td>رفع ملفات تقليدي</td><td>رفع ملفات لسيرفرات خاصة</td><td>رفع ملفات لسيرفرات خاصة</td><td><strong>ضغط وتشفير Base64 سحابي خفيف جداً</strong></td></tr>
    </tbody>
</table>

<h2>2.2 تميز الابتكار المطبق في تطبيق "شُغل"</h2>
<p>يتفوق تطبيق <strong>"شُغل"</strong> بكونه يركز على السرعة والفاعلية المباشرة، حيث يلغي الحاجة للإجراءات الورقية المعقدة، ويتيح للعميل إيجاد الخدمة الأقرب لميزانيته، وإمكانية الدردشة المباشرة الفورية داخل التطبيق بنظام الشات السحابي الحقيقي، فضلاً عن سهولة استخدام مزود الخدمة للوحة التحكم وإدارة الطلبات الواردة (قبول / رفض / إكمال) بزر واحد.</p>

<div class="page-break"></div>
<h1>الفصل الثالث: تحليل المتطلبات وحالات الاستخدام (Use Cases)</h1>

<h2>3.1 المتطلبات الوظيفية (Functional Requirements)</h2>
<ul>
    <li><strong>إدارة الحسابات والأدوار:</strong> إمكانية تحديد دور المستخدم (عميل أو مقدم خدمة) وتحديث بياناته الأساسية.</li>
    <li><strong>إدارة الخدمات:</strong> إمكانية إضافة خدمة جديدة شاملة السعر، الوصف، التصنيف، والصورة المرفقة، واستعراض كافة الخدمات المتاحة بالسوق.</li>
    <li><strong>نظام البحث المتقدم والمطابقة:</strong> البحث بنصوص الكلمات والميزانية المستهدفة والتصنيفات.</li>
    <li><strong>إدارة الطلبات والحالات:</strong> إرسال طلب جديد مع رقم التواصل، واستقبال الطلب لدى المزود مع خيارات (قبول/رفض/إكمال) وتحديث الحالة حياً.</li>
    <li><strong>المحادثات والدردشة الفورية:</strong> شات سحابي متكامل يربط الطرفين بالوقت الفعلي.</li>
</ul>

<h2>3.2 المتطلبات غير الوظيفية (Non-Functional Requirements)</h2>
<ul>
    <li><strong>الأداء والسرعة (Performance):</strong> زمن استجابة سريع ومزامنة حية دون تأخير زمني يذكر.</li>
    <li><strong>الموثوقية والاستمرارية (Reliability):</strong> تفعيل التخزين السحابي مع دعم Offline Persistence لتفادي ضياع البيانات.</li>
    <li><strong>سهولة الاستخدام (Usability):</strong> تصميم واجهات جليّة ونظيفة تعتمد Material Design 3 واللغة العربية بالكامل.</li>
</ul>

<div class="page-break"></div>
<h1>الفصل الرابع: المعمارية الهندسية الهيكلية وأنماط التصميم البرمجي</h1>

<h2>4.1 معمارية الطبقات النظيفة (Clean Architecture) والـ Repository Pattern</h2>
<p>تم تطبيق نمط <strong>Repository Pattern</strong> لفصل طبقة التحكم بالبيانات عن طبقة الواجهات والـ UI. يضمن هذا النمط جودة الكود البرمجي وسهولة الصيانة والاختبار، حيث يتعامل الـ Repository كمصدر موحد للبيانات (Single Source of Truth) يربط واجهات التطبيق بسحابة Firebase Realtime Database.</p>

<h2>4.2 أنماط التصميم المستعملة (Design Patterns)</h2>
<ul>
    <li><strong>Singleton Pattern:</strong> تم تطبيقه في صفوف <code>ServiceRepository</code> و <code>OrderRepository</code> و <code>FirebaseHelper</code> لضمان وجود كائن واحد فقط ينظم الاتصال بالقواعد طوال دورة حياة التطبيق.</li>
    <li><strong>Observer / Listener Pattern:</strong> استُعمل عبر الواجهات البرمجية <code>OnDataChangeListener</code> و <code>OnOrdersChangeListener</code> للأنصات للتغييرات السحابية وتحديث الـ UI فوراً.</li>
    <li><strong>Adapter Pattern:</strong> تم تطبيقه في صفوف <code>ServiceAdapter</code> و <code>OrderAdapter</code> و <code>MessageAdapter</code> لتحويل كائنات البيانات إلى مكونات مرئية داخل <code>RecyclerView</code>.</li>
</ul>

<div class="page-break"></div>
<h1>الفصل الخامس: هياكل البيانات والخوارزميات المبتكرة والتحليل التعقيدي</h1>

<h2>5.1 تحليل هياكل البيانات المعتمدة (Data Structures)</h2>
<p>تم استخدام ثلاثة هياكل بيانات رئيسية في المشروع بحسب متطلبات السرعة والأداء:</p>
<ol>
    <li><strong><code>ArrayList<T></code>:</strong> تخزين القوائم المتتابعة في المحولات لعرض عناصر التغذية البصرية في RecyclerView.</li>
    <li><strong><code>LinkedHashMap<K, V></code>:</strong> ربط المعرفات الفريدة UUID بالخدمات والطلبات لتسريع الوصول وعمليات التحديث $O(1)$ مع الحفاظ على الترتيب الزمني للإدخال.</li>
    <li><strong><code>HashSet<String></code>:</strong> تخزين كلمات البحث الفريدة وتفادي التكرار في SharedPreferences.</li>
</ol>

<h2>5.2 التحليل التعقيدي الزمني والمكاني للخوارزميات (Big-O Complexity Analysis)</h2>
<table>
    <thead>
        <tr>
            <th>العملية / الخوارزمية</th>
            <th>التعقيد الزمني (Time Complexity)</th>
            <th>التعقيد المكاني (Space Complexity)</th>
            <th>الشرح والتحليل</th>
        </tr>
    </thead>
    <tbody>
        <tr><td><strong>إضافة خدمة / طلب جديد</strong></td><td>$O(1)$</td><td>$O(1)$</td><td>إضافة مباشرة للـ LinkedHashMap والسحابة بفضل المعرفات الفريدة UUID.</td></tr>
        <tr><td><strong>البحث والفلترة النصية</strong></td><td>$O(N)$</td><td>$O(K)$</td><td>التكرار على $N$ من الخدمات المتاحة وتجميع $K$ من الخدمات المطابقة.</td></tr>
        <tr><td><strong>الترتيب حسب الميزانية</strong></td><td>$O(K \log K)$</td><td>$O(K)$</td><td>ترتيب القائمة المفلترة $K$ باستخدام خوارزمية الترتيب السريع المدمجة Dual-Pivot Quicksort.</td></tr>
        <tr><td><strong>ضغط وتحويل الصورة Base64</strong></td><td>$O(W \times H)$</td><td>$O(W \times H)$</td><td>قراءة أبعاد بكسلات الصورة $W \times H$ وتقليصها مصفوفياً وتشفيرها.</td></tr>
    </tbody>
</table>

<div class="page-break"></div>
<h1>الفصل السادس: التكامل والتواصل مع الخوادم السحابية Firebase</h1>

<h2>6.1 تهيئة السحابة وتسجيل الدخول التلقائي المجهول</h2>
<p>تتم عملية الشبك على خوادم **Firebase** عبر الصف المركزي `FirebaseHelper.java`. يضمن هذا الصف الاتصال التلقائي وتفعيل جلسة موثقة مجاناً عبر `signInAnonymously()` لتجاوز قيود القواعد واستيفاء شرط `auth != null` بدون إزعاج المستخدم بشرط التسجيل المعقد.</p>

<h2>6.2 إدارة قواعد البيانات السحابية بالوقت الفعلي Realtime Sync</h2>
<p>يقوم كل من `ServiceRepository` و `OrderRepository` بفتح مستمع سحابي حيي عبر `addValueEventListener()`. بمجرد إجراء أي كتابة على السحابة عبر `setValue()`، يتم إطلاق حدث `onDataChange()` تلقائياً على كل الأجهزة المتصلة لتحديث الواجهات فوراً.</p>

<div class="page-break"></div>
<h1>الفصل السابع: المخططات الهندسية البرمجية (UML Diagrams)</h1>

<h2>7.1 مخطط حالات الاستخدام الشامل (Use Case Diagram)</h2>
<div class="diagram-box">
+-----------------------------------------------------------------------------------+<br>
|                                 تطبيق شُغل WORK App                                |<br>
|                                                                                   |<br>
|   [العميل CLIENT]                                       [مقدم الخدمة PROVIDER]    |<br>
|          |                                                          |             |<br>
|          +---> (تسجيل الدخول واختيار الدور) <-------------------------+             |<br>
|          |                                                          |             |<br>
|          +---> (تصفح معرض الخدمات والتصنيفات) <-----------------------+             |<br>
|          |                                                          |             |<br>
|          +---> (البحث المتقدم والمطابقة بالميزانية $)               |             |<br>
|          |                                                          |             |<br>
|          +---> (إرسال طلب خدمة جديد مع رقم الهاتف)                  |             |<br>
|          |                                                          |             |<br>
|          +---> (متابعة حالات الطلبات حياً)                          |             |<br>
|          |                                                          |             |<br>
|          |                                (طرح خدمة جديدة مع الصور) <---+             |<br>
|          |                                                          |             |<br>
|          |                        (إدارة الطلبات الواردة قبول/رفض/إكمال) <--+             |<br>
|          |                                                          |             |<br>
|          +---> (الدردشة والمحادثات المباشرة In-App Chat) <-----------+             |<br>
|          |                                                          |             |<br>
|          +---> (التواصل الفوري المباشر عبر الواتساب والمكالمات) <-------+             |<br>
|                                                                                   |<br>
+-----------------------------------------------------------------------------------+
</div>

<div class="page-break"></div>
<h1>الفصل الثامن: التصميم البصري ونظام واجهات المستخدم Material Design 3</h1>

<h2>8.1 فلسفة التصميم ونظام الألوان</h2>
<p>تم اعتماد أحدث معايير **Material Design 3** الصادرة من Google لبناء تصميم عصري ومريح للعين:</p>
<ul>
    <li><strong>اللون البرتقالي الرئيسي (`#ea580c` / `#f97316`):</strong> يعبر عن النشاط والحيوية والعمل الحر الشغوف.</li>
    <li><strong>اللون الكحلي الداكن (`#0f172a` / `#1e293b`):</strong> يعكس الثقة والأمان والأكاديمية الرفيعة في البانرات العلوية.</li>
    <li><strong>الرمادي الفاتح للخلفيات (`#f8fafc` / `#f1f5f9`):</strong> يضمن تباين القراءة وإراحة عين المستخدم.</li>
    <li><strong>استخدام بطاقات `MaterialCardView`:</strong> ذات حواف منحنية (Corner Radius 12dp to 16dp) مع ظلال ناعمة وإطارات دقيقة.</li>
</ul>

<div class="page-break"></div>
<h1>الفصل التاسع: دليل الاستخدام والكتالوج التشغيلي (User Manual)</h1>

<h2>9.1 دليل رحلة العميل (Client Walkthrough)</h2>
<ol>
    <li>فتح التطبيق واختيار دور **"عميل"** بعد كتابة الاسم والبريد.</li>
    <li>تصفح الخدمات بحسب التصنيف (برمجة، تصميم، ترجمة، تسويق، صيانة) أو فتح شاشة البحث وإدخال الميزانية المتاحة بالدولار ($).</li>
    <li>النقر على الخدمة المناسبة لرؤية التفاصيل والصورة المرفقة.</li>
    <li>النقر على **"اطلب الآن"** وإدخال الاسم ورقم الهاتف/الواتساب للتأكيد.</li>
    <li>متابعة حالة الطلب في تبويب "طلباتي" والنقر على **"مراسلة بالتطبيق 💬"** للتحدث المباشر مع مقدم الخدمة.</li>
</ol>

<h2>9.2 دليل رحلة مقدم الخدمة (Provider Walkthrough)</h2>
<ol>
    <li>اختيار دور **"مقدم خدمة"** من شاشة الدخول.</li>
    <li>استعراض تبويب **"جميع الخدمات 🌐"** لرؤية كافة الخدمات المتاحة بالسوق.</li>
    <li>النقر على **"أضف خدمتك للعملاء"** لطرح خدمة جديدة وإرفاق صورة من المعرض وتحديد السعر بالدولار.</li>
    <li>فتح تبويب **"الطلبات الواردة 📥"** لرؤية طلبات العملاء الجدد مع خيارات **"قبول الطلب ✅"** أو **"رفض الطلب ❌"** أو **"إكمال الخدمة وتسليمها 🏁"**.</li>
    <li>النقر على **"مراسلة بالتطبيق 💬"** لبدء الدردشة الحية مع العميل صاحب الطلب.</li>
</ol>

<div class="page-break"></div>
<h1>الفصل العاشر: قواعد البيانات والشجرة السحابية NoSQL بالتفصيل</h1>

<h2>10.1 الهيكل السحابي الكامل لقواعد البيانات</h2>
<p>يتم تخزين كافة البيانات في سحابة **Firebase Realtime Database** كشجرة NoSQL ذات جودة عالية وتزامن لحظي:</p>

<pre><code>{
  "services": {
    "service_01_uuid": {
      "id": "service_01_uuid",
      "title": "تصميم شعار وهوية بصرية احترافية",
      "category": "تصميم",
      "description": "تصميم شعارات حديثة وبسيطة وسلم الملفات المصدري...",
      "price": 25.0,
      "providerName": "أحمد المهندس",
      "providerPhone": "0791234567",
      "rating": 5.0,
      "reviewsCount": 1,
      "imageResId": 2131230880,
      "imageBase64": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBD..."
    }
  },
  "orders": {
    "order_01_uuid": {
      "orderId": "order_01_uuid",
      "serviceItem": { "...": "..." },
      "clientName": "خالد العميل",
      "clientEmail": "client@example.com",
      "clientPhone": "0788889999",
      "orderDate": "2026-10-26",
      "status": "في انتظار موافقة مقدم الخدمة ⏳"
    }
  },
  "chats": {
    "chat_order_01_uuid": {
      "messages": {
        "msg_01_uuid": {
          "messageId": "msg_01_uuid",
          "chatId": "chat_order_01_uuid",
          "senderName": "خالد العميل",
          "senderRole": "client",
          "text": "مرحباً، هل يمكنك تنفيذ الشعار باللون الأزرق والبرتقالي؟",
          "timestamp": 1792900000000,
          "timeFormatted": "01:15 PM"
        }
      }
    }
  }
}</code></pre>

<div class="page-break"></div>
<h1>الفصل الحادي عشر: التوثيق البرمجي القياسي (JavaDocs)</h1>

<h2>11.1 توثيق الكود والمستودعات السحابية</h2>

<pre><code>/**
 * OrderRepository singleton managing placement, updates, and realtime synchronization
 * of client orders with Firebase Realtime Database.
 *
 * @author Student Developer
 * @version 1.0
 */
public class OrderRepository {

    /**
     * Listener interface for notifying UI when orders change in Firebase.
     */
    public interface OnOrdersChangeListener {
        /**
         * Fired when Firebase order snapshot updates.
         */
        void onOrdersChanged();
    }

    /**
     * Updates an order's status in Firebase Realtime Database and notifies local listeners.
     *
     * @param orderId   Unique UUID string for the target order.
     * @param newStatus New status string (e.g. OrderRepository.STATUS_ACCEPTED).
     */
    public void updateOrderStatus(String orderId, String newStatus) {
        // Method Implementation
    }
}</code></pre>

<div class="page-break"></div>
<h1>الفصل الثاني عشر: أثر المشروع، التقييم الذاتي، والمراجع</h1>

<h2>12.1 أثر المشروع والمهارات المكتسبة</h2>
<p>أكسبني تطوير هذا المشروع المتقدم مهارات هندسية وبرمجية رفيعة المستوى، شملت إتقان التطوير الجاوي المتقدم للاندرويد، وتعميق الفهم بالمعماريات النظيفة (Clean Architecture & Repository Pattern)، وكيفية تصميم وإدارة قواعد البيانات السحابية NoSQL وتفعيل المزامنة اللحظية الشفافة بين أجهزة متعددة. أتاح لي هذا المشروع فهم دورة حيوية كاملة للبرمجيات بدءاً من الفكرة وحتى التسليم والتوثيق الهندسية.</p>

<div class="note-box">
    <strong>🌟 كلمة تقدير وعرفان:</strong>
    <p>أكرر خالص الشكر والامتنان لأستاذي الفاضل <strong>أستاذ علي</strong> على جهوده المخلصة وتوجيهاته الدائمة لي طوال فترة تنفيذ هذا التقرير والتطبيق، والتي كان لها أبلغ الأثر في إنجاز هذا العمل المتكامل.</p>
</div>

<h2>12.2 المراجع العلمية المعتمدة (APA Citation Style)</h2>
<ol>
    <li>Android Developers. (2026). <em>Build Android apps with Kotlin and Java</em>. Google Developers. Retrieved from https://developer.android.com/docs</li>
    <li>Firebase Documentation. (2026). <em>Firebase Realtime Database for Android</em>. Google Cloud. Retrieved from https://firebase.google.com/docs/database/android/start</li>
    <li>Material Design. (2026). <em>Material 3 design guidelines for Android</em>. Google Design. Retrieved from https://m3.material.io/</li>
    <li>Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). <em>Design Patterns: Elements of Reusable Object-Oriented Software</em>. Addison-Wesley.</li>
</ol>

<div class="page-break"></div>
<h1>الملحق (أ): الأكواد المصدرية الكاملة للبرمجة (Java Source Code)</h1>
<p>يتضمن هذا الملحق النص المصدري الكامل لجميع الفئات والواجهات المبرمجة في مشروع تطبيق "شُغل - WORK":</p>

"@)

# Helper function to append file code
function Append-CodeBlock($title, $code) {
    $codeEscaped = Escape-Html $code
    $null = $sb.AppendLine("<h2>$title</h2>")
    $null = $sb.AppendLine("<pre><code>$codeEscaped</code></pre>")
}

Append-CodeBlock "1. MainActivity.java" $mainActivity
Append-CodeBlock "2. RoleSelectionActivity.java" $roleSelectionActivity
Append-CodeBlock "3. ClientMainActivity.java" $clientMainActivity
Append-CodeBlock "4. ProviderMainActivity.java" $providerMainActivity
Append-CodeBlock "5. SearchActivity.java" $searchActivity
Append-CodeBlock "6. AddServiceActivity.java" $addServiceActivity
Append-CodeBlock "7. ServiceDetailActivity.java" $serviceDetailActivity
Append-CodeBlock "8. ChatActivity.java" $chatActivity
Append-CodeBlock "9. ServiceRepository.java" $serviceRepository
Append-CodeBlock "10. OrderRepository.java" $orderRepository
Append-CodeBlock "11. FirebaseHelper.java" $firebaseHelper
Append-CodeBlock "12. SearchHistoryManager.java" $searchHistoryManager
Append-CodeBlock "13. ServiceItem.java" $serviceItem
Append-CodeBlock "14. OrderItem.java" $orderItem
Append-CodeBlock "15. ChatMessage.java" $chatMessage
Append-CodeBlock "16. ServiceAdapter.java" $serviceAdapter
Append-CodeBlock "17. OrderAdapter.java" $orderAdapter
Append-CodeBlock "18. MessageAdapter.java" $messageAdapter

$null = $sb.AppendLine(@"
<div class="page-break"></div>
<h1>الملحق (ب): الأكواد المصدرية الكاملة لتصميم الواجهات (XML Layouts)</h1>
<p>يتضمن هذا الملحق النص الكامل لملفات التنسيق والتصميم المرئي XML كافّة الواجهات والمكونات:</p>
"@)

Append-CodeBlock "1. activity_main.xml" $actMainXml
Append-CodeBlock "2. activity_role_selection.xml" $actRoleXml
Append-CodeBlock "3. activity_client_main.xml" $actClientXml
Append-CodeBlock "4. activity_provider_main.xml" $actProviderXml
Append-CodeBlock "5. activity_search.xml" $actSearchXml
Append-CodeBlock "6. activity_add_service.xml" $actAddServiceXml
Append-CodeBlock "7. activity_service_detail.xml" $actDetailXml
Append-CodeBlock "8. activity_chat.xml" $actChatXml
Append-CodeBlock "9. item_service.xml" $itemServiceXml
Append-CodeBlock "10. item_order.xml" $itemOrderXml
Append-CodeBlock "11. item_chat_message.xml" $itemChatXml

$null = $sb.AppendLine(@"
</body>
</html>
"@)

# Write out giant HTML
$sb.ToString() | Out-File -FilePath $htmlPath -Encoding utf8

Write-Host "Generated HTML at $htmlPath"

# Run Edge headless to convert to PDF
$edge = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
Start-Process -FilePath $edge -ArgumentList "--headless --disable-gpu --print-to-pdf=`"$pdfRoot`" `"$htmlPath`"" -Wait
Copy-Item -Path $pdfRoot -Destination $pdfArtifact -Force

Write-Host "PDF Generation Complete!"
