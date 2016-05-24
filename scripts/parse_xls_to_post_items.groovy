#!/usr/bin/env groovy

@Grab('org.apache.poi:poi:3.9')
@Grab('org.apache.poi:poi-ooxml:3.9')
@Grab('xml-apis:xml-apis:1.4.01')

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

if( !(args.length > 1) ) {
	println "Uso: archivo a procesar y userId"
	return
}

String fileName = args[0]
Integer userId = Integer.parseInt(args[1])

JsonSlurper slurper = new JsonSlurper()

if( !( new File(fileName).exists()) ) {
	println "Y el file tiene que existir..."
	return
}

new ExcelReader(fileName).eachLine {
	if( cell(0) ) {
  		println "curl -XPOST -d'${JsonOutput.toJson([title: cell(0).take(60), category_id: cell(6), currency_id: cell(2), available_quantity: cell(1), price: cell(3), buying_mode:"buy_it_now", listing_type_id:"gold_premium", condition:"new", warranty: "2 años", pictures:[[source: cell(4)]], description: cell(5) ])}' 'http://internal.mercadolibre.com/items/?caller.id=$userId'"
	}
}

