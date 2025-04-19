Комментарии по СreateMaterialService:
MaterialFiles - тип файлов, который возвращается после загрузки их с хранилища (вроде pdf но непонятно как хранить) 

ArrayList<MaterialFiles> materialFiles = storageService.getMaterialFiles(materials) 
возможно вынести внутрь pdfGeneratorService.generateCombinedPdf(materialFiles);

