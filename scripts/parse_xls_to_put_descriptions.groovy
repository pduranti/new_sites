#!/usr/bin/env groovy

@Grab('org.apache.poi:poi:3.9')
@Grab('org.apache.poi:poi-ooxml:3.9')
@Grab('xml-apis:xml-apis:1.0.b2')

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.DataFormatter
import groovy.json.*

class ExcelReader {

    def workbook
    def labels
    def row

    ExcelReader(String fileName) {
        DataFormatter dataFormatter = new DataFormatter()

        Row.metaClass.getAt = { int idx ->
            Cell cell = delegate.getCell(idx)

            if (!cell) {
                return null
            }

            return dataFormatter.formatCellValue(cell)
        }

        workbook = WorkbookFactory.create(new File(fileName))
    }

    def getSheet(idx) {
        def sheet
        if (!idx) idx = 0
        if (idx instanceof Number) {
            sheet = workbook.getSheetAt(idx)
        } else if (idx ==~ /^\d+$/) {
            sheet = workbook.getSheetAt(Integer.valueOf(idx))
        } else {
            sheet = workbook.getSheet(idx)
        }
        return sheet
    }

    def cell(idx) {
        if (labels && (idx instanceof String)) {
            idx = labels.indexOf(idx.toLowerCase())
        }
        return row[idx]
    }

    def propertyMissing(String name) {
        cell(name)
    }

    def eachLine(Map params = [:], Closure closure) {
        def offset = params.offset ?: 0
        def max = params.max ?: 9999999
        def sheet = getSheet(params.sheet)
        def rowIterator = sheet.rowIterator()
        def linesRead = 0

        if (params.labels) {
            labels = rowIterator.next().collect { it.toString().toLowerCase() }
        }
        offset.times { rowIterator.next() }

        closure.setDelegate(this)

        while (rowIterator.hasNext() && linesRead++ < max) {
            row = rowIterator.next()
            
            closure.call(row)
            
        }
    }
}

if( !(args.length > 0) ) {
	println "Tenés que pasarle el archivo a procesar :|"
	return
}

String fileName = args[0]

JsonSlurper slurper = new JsonSlurper()

if( !( new File(fileName).exists()) ) {
	println "Y el file tiene que existir..."
	return
}

new ExcelReader(fileName).eachLine {
	if( cell(0) ) {
  		println "curl -XPUT -d'${JsonOutput.toJson([text: cell(1)])}' 'http://internal.mercadolibre.com/items/${cell(0)}/description?caller.id=185432587'"
	}
}

