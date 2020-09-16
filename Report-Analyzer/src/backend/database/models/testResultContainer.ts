import { Schema, Document, MongooseDocument } from "mongoose";
import { ITimestamp } from './timestamps';
import { ITestResult } from './testResult';

export interface ITestResultContainer extends Document, ITimestamp {
  Identifier: string,
  ShortIdentifier: string,
  PcapStorageId: MongooseDocument['_id'],
  KeylogfileStorageId: MongooseDocument['_id'],
  Date: Date,
  DisplayName: string
  ElapsedTime: number,
  FailedTests: number,
  SucceededTests: number,
  DisabledTests: number,
  TestClasses?: ITestResultContainer[]
  TestResults: ITestResult[]
  TestResultClassMethodIndexMap: Map<string, number>,
  Score: {
    Security: {
      Reached: number,
      Total: number,
      Percentage: number,
    }
    Interoperability: {
      Reached: number,
      Total: number,
      Percentage: number
    }
  }
}


export const TestResultContainerSchema = new Schema({
  Identifier: {
    type: String,
    required: true,
  },
  PcapStorageId: {
    type: Schema.Types.ObjectId
  },
  KeylogfileStorageId: {
    type: Schema.Types.ObjectId
  },
  ShortIdentifier: String,
  Date: Date,
  DispalyName: String,
  ElapsedTime: Number,
  FailedTests: Number,
  SucceededTests: Number,
  DisabledTests: Number,
  Score: {
    Security: {
      Reached: {
        type: Number,
        default: 0
      },
      Total: {
        type: Number,
        default: 0
      },
      Percentage: {
        type: Number,
        default: 0
      },
    },
    Interoperability: {
      Reached: {
        type: Number,
        default: 0
      },
      Total: {
        type: Number,
        default: 0
      },
      Percentage: {
        type: Number,
        default: 0
      },
    }
  },
  TestResults: [{
    type: Schema.Types.ObjectId,
    ref: 'TestResult'
  }],
  TestResultClassMethodIndexMap: {
    type: Schema.Types.Map,
    of: Number,
    default: new Map()
  }
}, {
  timestamps: true
})

TestResultContainerSchema.index({Identifier: 1})
TestResultContainerSchema.index({PcapStorageId: 1})
TestResultContainerSchema.index({KeylogfileStorageId: 1})
