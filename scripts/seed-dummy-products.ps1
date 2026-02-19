param(
  [Parameter(Mandatory = $true)]
  [string]$Email,

  [Parameter(Mandatory = $true)]
  [string]$Password,

  [string]$GatewayUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "Logging in as $Email ..."
$loginBody = @{ email = $Email; password = $Password } | ConvertTo-Json
$login = Invoke-RestMethod -Uri "$GatewayUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"

if (-not $login.accessToken) {
  throw "Login failed: no access token returned."
}

if ($login.role -ne "VENDOR") {
  throw "Login succeeded but role is '$($login.role)'. VENDOR role is required to seed products."
}

$headers = @{ Authorization = "Bearer $($login.accessToken)" }

$products = @(
  @{ name = "Industrial Safety Helmet"; description = "High-impact ABS helmet with adjustable strap for factory floor safety."; price = 899; stock = 120; category = "Safety" },
  @{ name = "Nitrile Protective Gloves Pack"; description = "Chemical-resistant disposable gloves suitable for maintenance and assembly work."; price = 1249; stock = 80; category = "Safety" },
  @{ name = "Heavy Duty Air Compressor 2HP"; description = "Low-noise compressor built for workshops and pneumatic tool operations."; price = 18999; stock = 15; category = "Machinery" },
  @{ name = "MIG Welding Machine 250A"; description = "Compact welding unit with stable arc output for fabrication workloads."; price = 32499; stock = 9; category = "Machinery" },
  @{ name = "Stainless Steel Fastener Kit"; description = "Assorted bolts, nuts, and washers for industrial assembly and repair tasks."; price = 2499; stock = 200; category = "Hardware" },
  @{ name = "EPDM Conveyor Belt Roll"; description = "Wear-resistant conveyor belt for medium-load production line usage."; price = 14200; stock = 27; category = "Material Handling" },
  @{ name = "Digital Clamp Meter"; description = "TRMS clamp meter for accurate current readings in industrial panels."; price = 3599; stock = 52; category = "Electrical" },
  @{ name = "Three-Phase Contactor 32A"; description = "Reliable switching contactor for motor control and protection systems."; price = 1890; stock = 95; category = "Electrical" }
)

$created = 0
foreach ($product in $products) {
  $payload = $product | ConvertTo-Json
  try {
    Invoke-RestMethod -Uri "$GatewayUrl/api/products" -Method Post -Headers $headers -Body $payload -ContentType "application/json" | Out-Null
    $created++
    Write-Host "Created: $($product.name)"
  } catch {
    $msg = $_.Exception.Message
    if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
      $msg = $_.ErrorDetails.Message
    }
    Write-Warning "Skipped: $($product.name) -> $msg"
  }
}

Write-Host ""
Write-Host "Seed completed. Created $created / $($products.Count) products."
