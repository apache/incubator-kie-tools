$files = @(Get-ChildItem dist\*.tar.gz)
foreach ($file in $files) {
  $sha = $(certutil -hashfile $file.FullName SHA256)[1]
  $filePath = "dist\" + $file.Name + ".sha256"
  Write-Output $sha | Out-File -FilePath $filePath
}